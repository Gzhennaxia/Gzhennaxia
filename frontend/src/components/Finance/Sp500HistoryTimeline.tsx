import React, { useMemo } from 'react';
import { Card, Tag, Typography } from 'antd';
import type { Dayjs } from 'dayjs';
import {
  SP500_HISTORY_EVENTS,
  formatSp500EventDate,
  getSp500EventColor,
  getSp500EventFocusRange,
  isSp500EventInRange,
  type Sp500HistoryEvent,
} from './sp500HistoryEvents';

export interface Sp500HistoryTimelineProps {
  /** 当前图表查询区间，用于高亮可见节点 */
  range: [Dayjs, Dayjs];
  /** 点击节点时聚焦图表 */
  onFocusRange: (range: [Dayjs, Dayjs]) => void;
}

/**
 * 标普 500 历史重要节点横向时间轴（叙事参考 TimelineJS，React + Ant Design 实现）。
 */
const Sp500HistoryTimeline: React.FC<Sp500HistoryTimelineProps> = ({ range, onFocusRange }) => {
  const events = useMemo(
    () =>
      [...SP500_HISTORY_EVENTS].sort(
        (a, b) => dayjsCompare(a.startDate, b.startDate),
      ),
    [],
  );

  const handleClick = (event: Sp500HistoryEvent) => {
    onFocusRange(getSp500EventFocusRange(event));
  };

  return (
    <Card
      title="历史重要节点"
      style={{ marginTop: 16 }}
      extra={
        <Typography.Text type="secondary" style={{ fontSize: 12 }}>
          点击节点可聚焦图表区间 · 样式参考 Knight Lab TimelineJS
        </Typography.Text>
      }
    >
      <div
        style={{
          overflowX: 'auto',
          paddingBottom: 4,
          WebkitOverflowScrolling: 'touch',
        }}
      >
        <div
          style={{
            position: 'relative',
            display: 'flex',
            minWidth: events.length * 236,
            paddingTop: 8,
            paddingBottom: 8,
          }}
        >
          <div
            aria-hidden
            style={{
              position: 'absolute',
              top: 28,
              left: 24,
              right: 24,
              height: 2,
              background: 'linear-gradient(90deg, #d9d9d9 0%, #bfbfbf 50%, #d9d9d9 100%)',
              borderRadius: 1,
            }}
          />
          {events.map((event) => {
            const active = isSp500EventInRange(event, range);
            const color = getSp500EventColor(event.category);
            return (
              <button
                key={event.id}
                type="button"
                onClick={() => handleClick(event)}
                style={{
                  flex: '0 0 236px',
                  padding: '0 12px',
                  border: 'none',
                  background: 'transparent',
                  cursor: 'pointer',
                  textAlign: 'left',
                  outline: 'none',
                }}
              >
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'flex-start',
                    gap: 6,
                    padding: '12px 10px',
                    borderRadius: 8,
                    border: active ? `1px solid ${color}` : '1px solid transparent',
                    background: active ? `${color}08` : 'transparent',
                    transition: 'background 0.2s, border-color 0.2s',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, width: '100%' }}>
                    <span
                      aria-hidden
                      style={{
                        width: 12,
                        height: 12,
                        borderRadius: '50%',
                        background: color,
                        boxShadow: active ? `0 0 0 3px ${color}33` : 'none',
                        flexShrink: 0,
                      }}
                    />
                    <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                      {formatSp500EventDate(event)}
                    </Typography.Text>
                  </div>
                  <Typography.Text strong style={{ fontSize: 14, color: active ? color : undefined }}>
                    {event.title}
                  </Typography.Text>
                  <Typography.Paragraph
                    type="secondary"
                    style={{ marginBottom: 0, fontSize: 12, lineHeight: 1.5 }}
                    ellipsis={{ rows: 3, expandable: true, symbol: '展开' }}
                  >
                    {event.summary}
                  </Typography.Paragraph>
                  <Tag color={color} style={{ marginInlineEnd: 0 }}>
                    {event.category}
                  </Tag>
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </Card>
  );
};

function dayjsCompare(a: string, b: string): number {
  return a.localeCompare(b);
}

export default Sp500HistoryTimeline;
