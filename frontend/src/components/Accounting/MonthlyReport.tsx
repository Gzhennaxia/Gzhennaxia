import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, DatePicker, Table, Tag, Spin, message } from 'antd';
import dayjs, { Dayjs } from 'dayjs';
import { accountingService } from '../../services/accountingService';
import type { MonthlyReportVO } from '../../types/Accounting';

const MonthlyReport: React.FC = () => {
  const [month, setMonth] = useState<Dayjs>(dayjs());
  const [report, setReport] = useState<MonthlyReportVO | null>(null);
  const [loading, setLoading] = useState(false);

  const load = (m: Dayjs) => {
    setLoading(true);
    accountingService
      .getMonthlyReport(m.year(), m.month() + 1)
      .then(setReport)
      .catch((e: { message?: string }) => message.error(e.message || '加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load(month);
  }, [month]);

  return (
    <>
      <DatePicker picker="month" value={month} onChange={(v) => v && setMonth(v)} style={{ marginBottom: 24 }} />
      {loading ? (
        <Spin />
      ) : (
        <>
          <Row gutter={16} style={{ marginBottom: 24 }}>
            <Col span={6}><Card><Statistic title="收入" value={report?.income ?? 0} precision={2} prefix="¥" valueStyle={{ color: '#3f8600' }} /></Card></Col>
            <Col span={6}><Card><Statistic title="支出" value={report?.expense ?? 0} precision={2} prefix="¥" valueStyle={{ color: '#cf1322' }} /></Card></Col>
            <Col span={6}><Card><Statistic title="结余" value={report?.balance ?? 0} precision={2} prefix="¥" /></Card></Col>
            <Col span={6}><Card><Statistic title="储蓄率" value={report?.savingsRate ?? 0} precision={1} suffix="%" /></Card></Col>
          </Row>
          <Card title="支出分类">
            <Table
              rowKey="categoryId"
              pagination={false}
              dataSource={report?.expenseByCategory ?? []}
              columns={[
                { title: '分类', dataIndex: 'categoryName' },
                { title: '金额', dataIndex: 'amount', render: (v: number) => `¥ ${v.toFixed(2)}` },
                { title: '月预算', dataIndex: 'budgetMonthly', render: (v?: number) => (v != null ? `¥ ${v}` : '-') },
                {
                  title: '状态',
                  render: (_, r) => (r.overBudget ? <Tag color="red">超预算</Tag> : <Tag color="green">正常</Tag>),
                },
              ]}
            />
          </Card>
        </>
      )}
    </>
  );
};

export default MonthlyReport;
