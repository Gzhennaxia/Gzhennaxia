import dayjs, { Dayjs } from 'dayjs';
import type { MarketDailyPoint } from '../../types/Market';

/** 折线图单点（x 为 Date，供 G2 时间轴解析） */
export interface Sp500ChartPoint {
  date: Date;
  close: number;
}

/** 快捷区间：近 N 年/月 */
export const SP500_RANGE_PRESETS: { label: string; value: [Dayjs, Dayjs] }[] = [
  { label: '近1月', value: [dayjs().subtract(1, 'month'), dayjs()] },
  { label: '近6月', value: [dayjs().subtract(6, 'month'), dayjs()] },
  { label: '近1年', value: [dayjs().subtract(1, 'year'), dayjs()] },
  { label: '近2年', value: [dayjs().subtract(2, 'year'), dayjs()] },
  { label: '近5年', value: [dayjs().subtract(5, 'year'), dayjs()] },
  { label: '近10年', value: [dayjs().subtract(10, 'year'), dayjs()] },
  { label: '近15年', value: [dayjs().subtract(15, 'year'), dayjs()] },
  { label: '近20年', value: [dayjs().subtract(20, 'year'), dayjs()] },
];

/** 与离线 CSV 最早交易日一致，用于 MAX 全量区间 */
export const SP500_EARLIEST_DATE = '1979-12-26';

export const SP500_MAX_LABEL = 'MAX';

/** 库内可查全量区间：最早历史日 → 今日 */
export function getSp500MaxRange(): [Dayjs, Dayjs] {
  return [dayjs(SP500_EARLIEST_DATE), dayjs()];
}

export function isSp500MaxRange(range: [Dayjs, Dayjs]): boolean {
  const [start, end] = getSp500MaxRange();
  return range[0].isSame(start, 'day') && range[1].isSame(end, 'day');
}

export function isSameDayRange(a: [Dayjs, Dayjs], b: [Dayjs, Dayjs]): boolean {
  return a[0].isSame(b[0], 'day') && a[1].isSame(b[1], 'day');
}

/**
 * 按时间跨度与点数计算横轴刻度，避免长区间标签重叠。
 */
export function buildSp500XAxisConfig(range: [Dayjs, Dayjs], pointCount: number) {
  const spanDays = Math.max(1, range[1].diff(range[0], 'day'));
  const spanYears = spanDays / 365;

  let tickCount: number;
  if (spanDays <= 35) {
    tickCount = 6;
  } else if (spanDays <= 180) {
    tickCount = 6;
  } else if (spanYears <= 2) {
    tickCount = 8;
  } else if (spanYears <= 5) {
    tickCount = 7;
  } else if (spanYears <= 10) {
    tickCount = 8;
  } else if (spanYears <= 15) {
    tickCount = 9;
  } else {
    tickCount = 10;
  }

  if (pointCount > 2000) {
    tickCount = Math.min(tickCount, 8);
  }
  if (pointCount > 5000) {
    tickCount = Math.min(tickCount, 6);
  }

  return {
    tickCount,
    autoRotate: spanYears > 2,
    spanYears,
  };
}

/** 横轴刻度标签（兼容 Date / 时间戳 / 字符串） */
export function formatSp500XLabel(value: string | number | Date, spanYears: number): string {
  const d = dayjs(value);
  if (!d.isValid()) {
    return String(value);
  }
  if (spanYears > 8) {
    return d.format('YYYY');
  }
  if (spanYears > 2) {
    return d.format('YYYY-MM');
  }
  if (spanYears > 1) {
    return d.format('YY-MM');
  }
  return d.format('MM-DD');
}

/**
 * 将 API 点列转为图表数据：时间升序、过滤无效收盘价。
 */
export function buildSp500ChartPoints(points: MarketDailyPoint[]): Sp500ChartPoint[] {
  return points
    .map((p) => {
      const d = dayjs(p.date);
      const close = Number(p.close);
      if (!d.isValid() || Number.isNaN(close)) {
        return null;
      }
      return { date: d.toDate(), close };
    })
    .filter((p): p is Sp500ChartPoint => p !== null)
    .sort((a, b) => a.date.getTime() - b.date.getTime());
}

/**
 * 横轴时间域：贴合首尾数据，避免 nice 在两侧留出大块空白。
 */
export function buildSp500XScaleConfig(
  points: Sp500ChartPoint[],
  tickCount: number,
): Record<string, unknown> {
  const base = {
    type: 'time',
    tickCount,
    nice: false,
  };
  if (points.length === 0) {
    return base;
  }
  const min = points[0].date;
  const max = points[points.length - 1].date;
  if (points.length === 1) {
    return {
      ...base,
      domain: [dayjs(min).subtract(1, 'day').toDate(), dayjs(max).add(1, 'day').toDate()],
    };
  }
  return {
    ...base,
    domain: [min, max],
  };
}

/** 长区间启用底部滑块，便于在大量数据下缩放查看 */
export function shouldShowSp500Slider(range: [Dayjs, Dayjs], pointCount: number): boolean {
  const spanYears = range[1].diff(range[0], 'day') / 365;
  return spanYears > 3 || pointCount > 800;
}
