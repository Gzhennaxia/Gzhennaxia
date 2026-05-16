import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, Spin, message } from 'antd';
import { accountingService } from '../../services/accountingService';
import type { PositionVO } from '../../types/Accounting';

const InvestmentOverview: React.FC = () => {
  const [positions, setPositions] = useState<PositionVO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    accountingService
      .listPositions()
      .then(setPositions)
      .catch((e: { message?: string }) => message.error(e.message || '加载失败'))
      .finally(() => setLoading(false));
  }, []);

  const totalMarket = positions.reduce((s, p) => s + (p.marketValue ?? 0), 0);
  const totalPnl = positions.reduce((s, p) => s + (p.unrealizedPnl ?? 0), 0);

  if (loading) {
    return <Spin style={{ display: 'block', margin: '80px auto' }} />;
  }

  return (
    <>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card><Statistic title="总市值" value={totalMarket} precision={2} prefix="¥" /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="浮动盈亏" value={totalPnl} precision={2} prefix="¥" valueStyle={{ color: totalPnl >= 0 ? '#cf1322' : '#3f8600' }} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="持仓数" value={positions.length} /></Card>
        </Col>
      </Row>
      <Card title="资产分布（按标的）">
        {positions.map((p) => (
          <Row key={p.symbolId} style={{ marginBottom: 8 }}>
            <Col span={12}>{p.name} ({p.symbol})</Col>
            <Col span={6}>市值 ¥{p.marketValue?.toFixed(2) ?? '-'}</Col>
            <Col span={6}>占比 {p.weightPercent ?? 0}%</Col>
          </Row>
        ))}
        {positions.length === 0 && <p style={{ color: '#999' }}>暂无持仓，请先录入投资交易</p>}
      </Card>
    </>
  );
};

export default InvestmentOverview;
