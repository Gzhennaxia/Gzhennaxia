import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, Table, Button, Spin, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { accountingService } from '../../services/accountingService';
import type { DashboardVO } from '../../types/Accounting';

const AccountingDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<DashboardVO | null>(null);

  useEffect(() => {
    accountingService
      .getDashboard()
      .then(setData)
      .catch((e: { message?: string }) => message.error(e.message || '加载失败'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Spin style={{ display: 'block', margin: '80px auto' }} />;
  }

  return (
    <>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card><Statistic title="净资产" value={data?.netWorth ?? 0} precision={2} prefix="¥" /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="流动资金" value={data?.liquidAssets ?? 0} precision={2} prefix="¥" /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="本月收入" value={data?.monthIncome ?? 0} precision={2} prefix="¥" valueStyle={{ color: '#3f8600' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="本月支出" value={data?.monthExpense ?? 0} precision={2} prefix="¥" valueStyle={{ color: '#cf1322' }} /></Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card title="投资市值"><Statistic value={data?.investmentValue ?? 0} precision={2} prefix="¥" /></Card>
        </Col>
        <Col span={8}>
          <Card title="储蓄率"><Statistic value={data?.savingsRate ?? 0} precision={1} suffix="%" /></Card>
        </Col>
        <Col span={8}>
          <Card>
            <Button type="primary" onClick={() => navigate('/accounting/add')}>记一笔</Button>
            <Button style={{ marginLeft: 8 }} onClick={() => navigate('/accounting/reports')}>月报</Button>
          </Card>
        </Col>
      </Row>
      <Card title="最近流水">
        <Table
          rowKey="id"
          pagination={false}
          dataSource={data?.recentTransactions ?? []}
          columns={[
            { title: '时间', dataIndex: 'tradeTime', width: 180 },
            { title: '类型', dataIndex: 'type', width: 80 },
            { title: '分类', dataIndex: 'categoryName' },
            { title: '账户', dataIndex: 'accountName' },
            {
              title: '金额',
              dataIndex: 'amount',
              render: (v: number, r) => {
                const color = r.type === 'income' ? '#3f8600' : r.type === 'expense' ? '#cf1322' : '#666';
                const prefix = r.type === 'income' ? '+' : r.type === 'expense' ? '-' : '';
                return <span style={{ color }}>{prefix}{v}</span>;
              },
            },
          ]}
        />
      </Card>
    </>
  );
};

export default AccountingDashboard;
