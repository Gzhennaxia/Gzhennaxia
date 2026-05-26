#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 docs/finance/美国标准普尔500指数历史数据.csv 导入 fin_market_daily（标的 SP500）。

依赖：pip install psycopg2-binary

用法示例：
  python scripts/finance/import_sp500_csv.py
  python scripts/finance/import_sp500_csv.py --csv docs/finance/美国标准普尔500指数历史数据.csv
  python scripts/finance/import_sp500_csv.py --dry-run

数据库连接（任选其一）：
  环境变量 PGHOST / PGPORT / PGDATABASE / PGUSER / PGPASSWORD
  或参数 --host --port --dbname --user --password
"""

from __future__ import annotations

import argparse
import csv
import os
import sys
from datetime import date
from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Iterable, Optional

try:
    import psycopg2
    from psycopg2.extras import execute_batch
except ImportError:
    print("请先安装依赖: pip install psycopg2-binary", file=sys.stderr)
    sys.exit(1)

SOURCE_CSV = "CSV"
SYMBOL = "SP500"
DEFAULT_BATCH = 2000

UPSERT_SQL = """
INSERT INTO fin_market_daily (
    symbol_id, trade_date, open_price, high_price, low_price, close_price, volume, source
) VALUES (
    %(symbol_id)s, %(trade_date)s, %(open_price)s, %(high_price)s,
    %(low_price)s, %(close_price)s, %(volume)s, %(source)s
)
ON CONFLICT (symbol_id, trade_date) DO UPDATE SET
    open_price  = EXCLUDED.open_price,
    high_price  = EXCLUDED.high_price,
    low_price   = EXCLUDED.low_price,
    close_price = EXCLUDED.close_price,
    volume      = EXCLUDED.volume,
    source      = EXCLUDED.source,
    update_time = CURRENT_TIMESTAMP
"""

ENSURE_SYMBOL_SQL = """
INSERT INTO fin_market_symbol (symbol, name, market_type, currency, data_source, external_id)
SELECT %(symbol)s, '标普 500', 'INDEX', 'USD', 'CSV', 'SP500'
WHERE NOT EXISTS (SELECT 1 FROM fin_market_symbol WHERE symbol = %(symbol)s)
"""

SELECT_SYMBOL_ID_SQL = "SELECT id FROM fin_market_symbol WHERE symbol = %(symbol)s LIMIT 1"


def repo_root() -> Path:
    return Path(__file__).resolve().parents[2]


def default_csv_path() -> Path:
    return repo_root() / "docs" / "finance" / "美国标准普尔500指数历史数据.csv"


def parse_decimal(raw: Optional[str]) -> Optional[Decimal]:
    if raw is None:
        return None
    text = raw.strip().replace(",", "").replace("%", "")
    if not text or text == "-":
        return None
    try:
        return Decimal(text)
    except InvalidOperation:
        return None


def parse_volume(raw: Optional[str]) -> Optional[int]:
    value = parse_decimal(raw)
    if value is None:
        return None
    return int(value)


def parse_trade_date(raw: str) -> Optional[date]:
    text = raw.strip()
    if not text:
        return None
    parts = text.split("/")
    if len(parts) != 3:
        return None
    try:
        year, month, day = int(parts[0]), int(parts[1]), int(parts[2])
        return date(year, month, day)
    except ValueError:
        return None


def read_rows(csv_path: Path, symbol_id: int) -> Iterable[dict]:
    with csv_path.open("r", encoding="utf-8-sig", newline="") as fp:
        reader = csv.DictReader(fp)
        if not reader.fieldnames or "日期" not in reader.fieldnames:
            raise ValueError("CSV 缺少表头「日期」，请确认文件格式")

        for line_no, row in enumerate(reader, start=2):
            trade_date = parse_trade_date(row.get("日期", ""))
            close_price = parse_decimal(row.get("收盘"))
            if trade_date is None or close_price is None:
                continue

            yield {
                "symbol_id": symbol_id,
                "trade_date": trade_date,
                "open_price": parse_decimal(row.get("开盘")),
                "high_price": parse_decimal(row.get("高")),
                "low_price": parse_decimal(row.get("低")),
                "close_price": close_price,
                "volume": parse_volume(row.get("交易量")),
                "source": SOURCE_CSV,
                "_line": line_no,
            }


def connect_db(args: argparse.Namespace):
    return psycopg2.connect(
        host=args.host,
        port=args.port,
        dbname=args.dbname,
        user=args.user,
        password=args.password,
    )


def resolve_symbol_id(conn, symbol: str) -> int:
    with conn.cursor() as cur:
        cur.execute(ENSURE_SYMBOL_SQL, {"symbol": symbol})
        cur.execute(SELECT_SYMBOL_ID_SQL, {"symbol": symbol})
        row = cur.fetchone()
        if not row:
            raise RuntimeError("无法获取 fin_market_symbol.id，请先执行 finance-market.sql")
        return int(row[0])


def flush_batch(conn, batch: list[dict], dry_run: bool) -> int:
    if dry_run:
        return len(batch)
    with conn.cursor() as cur:
        execute_batch(cur, UPSERT_SQL, batch, page_size=len(batch))
    return len(batch)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="导入标普 500 CSV 到 fin_market_daily")
    parser.add_argument(
        "--csv",
        type=Path,
        default=default_csv_path(),
        help="CSV 文件路径（默认 docs/finance/美国标准普尔500指数历史数据.csv）",
    )
    parser.add_argument("--symbol", default=SYMBOL, help="标的代码，默认 SP500")
    parser.add_argument("--batch", type=int, default=DEFAULT_BATCH, help="每批提交行数")
    parser.add_argument("--dry-run", action="store_true", help="只解析不写入数据库")
    parser.add_argument("--host", default=os.getenv("PGHOST", "127.0.0.1"))
    parser.add_argument("--port", default=os.getenv("PGPORT", "5432"))
    parser.add_argument("--dbname", default=os.getenv("PGDATABASE", "gzhennaxia"))
    parser.add_argument("--user", default=os.getenv("PGUSER", "postgres"))
    parser.add_argument("--password", default=os.getenv("PGPASSWORD", ""))
    return parser


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()

    csv_path: Path = args.csv
    if not csv_path.is_file():
        print(f"找不到 CSV: {csv_path}", file=sys.stderr)
        return 1

    print(f"CSV: {csv_path}")
    print(f"标的: {args.symbol}, dry_run={args.dry_run}")

    conn = connect_db(args)
    try:
        symbol_id = resolve_symbol_id(conn, args.symbol)
        print(f"symbol_id={symbol_id}")

        batch: list[dict] = []
        total = 0
        for item in read_rows(csv_path, symbol_id):
            batch.append({k: v for k, v in item.items() if k != "_line"})
            if len(batch) >= args.batch:
                total += flush_batch(conn, batch, args.dry_run)
                batch.clear()
                print(f"  已处理 {total} 行...", flush=True)

        if batch:
            total += flush_batch(conn, batch, args.dry_run)

        if not args.dry_run:
            conn.commit()
        print(f"完成：写入/更新 {total} 条日 K（source={SOURCE_CSV}）")
    except Exception as ex:
        conn.rollback()
        print(f"导入失败: {ex}", file=sys.stderr)
        return 1
    finally:
        conn.close()

    return 0


if __name__ == "__main__":
    sys.exit(main())
