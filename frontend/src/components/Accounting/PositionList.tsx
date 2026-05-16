import React, { useEffect, useState } from 'react';
import { Table, message } from 'antd';
import { accountingService } from '../../services/accountingService';
import type { PositionVO } from '../../types/Accounting';

const PositionList: React.FC = () => {
  const [data, setData] = useState<PositionVO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    accountingService
      .listPositions()
      .then(setData)
      .catch((e: { message?: string }) => message.error(e.message || '加载失败'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Table
      rowKey="symbolId"
      loading={loading}
      dataSource={data}
      columns={[
        { title: '代码', dataIndex: 'symbol' },
        { title: '名称', dataIndex: 'name' },
        { title: '市场', dataIndex: 'market' },
        { title: '数量', dataIndex: 'quantity' },
        { title: '成本价', dataIndex: 'avgCost', render: (v: number) => v?.toFixed(4) },
        { title: '现价', dataIndex: 'currentPrice', render: (v: number) => v?.toFixed(4) ?? '-' },
        { title: '市值', dataIndex: 'marketValue', render: (v: number) => v?.toFixed(2) },
        {
          title: '盈亏',
          render: (_, r) => (
            <span style={{ color: (r.unrealizedPnl ?? 0) >= 0 ? '#cf1322' : '#3f8600' }}>
              {r.unrealizedPnl?.toFixed(2)} ({r.unrealizedPnlPercent}%)
            </span>
          ),
        },
        { title: '占比', dataIndex: 'weightPercent', render: (v: number) => `${v ?? 0}%` },
      ]}
    />
  );
};

export default PositionList;
