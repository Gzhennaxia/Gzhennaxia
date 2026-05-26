import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Button, Card, DatePicker, Empty, Space, Spin, message } from 'antd';
import { Line } from '@ant-design/charts';
import { SyncOutlined } from '@ant-design/icons';
import dayjs, { Dayjs } from 'dayjs';
import type { MarketDailyChartVO } from '../../types/Market';
import { marketService } from '../../services/marketService';
import {
  SP500_RANGE_PRESETS,
  buildSp500ChartPoints,
  buildSp500XAxisConfig,
  buildSp500XScaleConfig,
  formatSp500XLabel,
  SP500_MAX_LABEL,
  getSp500MaxRange,
  isSameDayRange,
  isSp500MaxRange,
  shouldShowSp500Slider,
} from './sp500ChartAxis';
import Sp500HistoryTimeline from './Sp500HistoryTimeline';

const { RangePicker } = DatePicker;

const SYMBOL = 'SP500';

/**
 * 标普 500 指数历史走势（FRED 入库数据）。
 */
const Sp500IndexPage: React.FC = () => {
  const [range, setRange] = useState<[Dayjs, Dayjs]>([dayjs().subtract(1, 'year'), dayjs()]);
  const [chart, setChart] = useState<MarketDailyChartVO | null>(null);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  const loadChart = useCallback(async () => {
    try {
      setLoading(true);
      const data = await marketService.getDaily(
        SYMBOL,
        range[0].format('YYYY-MM-DD'),
        range[1].format('YYYY-MM-DD'),
      );
      setChart(data);
    } catch (e) {
      console.error(e);
      message.error('加载行情失败，请先同步数据或检查后端配置');
      setChart(null);
    } finally {
      setLoading(false);
    }
  }, [range]);

  useEffect(() => {
    loadChart();
  }, [loadChart]);

  const handleSync = async () => {
    try {
      setSyncing(true);
      const result = await marketService.sync(SYMBOL);
      if (result.status === 'SUCCESS') {
        message.success(result.message || `同步 ${result.rowsAffected} 条`);
        await loadChart();
      } else {
        message.error(result.message || '同步失败');
      }
    } catch (e) {
      console.error(e);
      message.error('同步请求失败，请检查 FRED API Key 配置');
    } finally {
      setSyncing(false);
    }
  };

  const chartPoints = useMemo(
    () => buildSp500ChartPoints(chart?.points ?? []),
    [chart],
  );

  const lineConfig = useMemo(() => {
    const pointCount = chartPoints.length;
    const xAxis = buildSp500XAxisConfig(range, pointCount);
    const showSlider = shouldShowSp500Slider(range, pointCount);

    return {
      data: chartPoints,
      xField: 'date',
      yField: 'close',
      smooth: true,
      height: showSlider ? 440 : 400,
      marginTop: 12,
      marginRight: 8,
      marginBottom: showSlider ? 52 : 28,
      marginLeft: 52,
      scale: {
        x: buildSp500XScaleConfig(chartPoints, xAxis.tickCount),
        y: { nice: true },
      },
      style: {
        insetLeft: 0,
        insetRight: 0,
      },
      axis: {
        x: {
          labelAutoHide: true,
          labelAutoRotate: xAxis.autoRotate,
          labelFormatter: (v: string | number | Date) => formatSp500XLabel(v, xAxis.spanYears),
        },
        y: {
          labelFormatter: (v: string) => Number(v).toLocaleString(),
        },
      },
      ...(showSlider
        ? {
            slider: {
              x: {
                height: 24,
                labelFormatter: (v: string | number | Date) =>
                  dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD') : String(v),
              },
            },
          }
        : {}),
      interaction: {
        tooltip: {
          title: (d: { date: Date }) => dayjs(d.date).format('YYYY-MM-DD'),
          items: [
            {
              channel: 'y',
              name: '收盘',
              valueFormatter: (v: number) =>
                Number(v).toLocaleString(undefined, { maximumFractionDigits: 2 }),
            },
          ],
        },
      },
    };
  }, [chartPoints, range]);

  const hasData = chartPoints.length > 0;

  return (
    <div>
      <Space style={{ marginBottom: 16, flexWrap: 'wrap' }} size="middle">
        <RangePicker
          value={range}
          onChange={(v) => v && setRange([v[0]!, v[1]!])}
          allowClear={false}
        />
        <Space size="small" wrap>
          {SP500_RANGE_PRESETS.map((p) => (
            <Button
              key={p.label}
              size="small"
              type={isSameDayRange(range, p.value) ? 'primary' : 'default'}
              onClick={() => setRange(p.value)}
            >
              {p.label}
            </Button>
          ))}
          <Button
            key={SP500_MAX_LABEL}
            size="small"
            type={isSp500MaxRange(range) ? 'primary' : 'default'}
            onClick={() => setRange(getSp500MaxRange())}
          >
            {SP500_MAX_LABEL}
          </Button>
        </Space>
        <Button type="primary" icon={<SyncOutlined />} loading={syncing} onClick={handleSync}>
          从 FRED 同步数据
        </Button>
        <Button onClick={loadChart} loading={loading}>
          刷新图表
        </Button>
      </Space>

      <Card title={chart ? `${chart.name}（${chart.symbol}）` : '标普 500'}>
        <Spin spinning={loading}>
          {hasData ? (
            <Line {...lineConfig} />
          ) : (
            <Empty description="当前区间无数据。若已导入 CSV，请点「MAX」或「近20年」；或配置 FRED Key 后同步" />
          )}
        </Spin>
      </Card>

      <Sp500HistoryTimeline range={range} onFocusRange={setRange} />
    </div>
  );
};

export default Sp500IndexPage;
