import React, { useEffect, useState } from 'react';
import { Table, Select, Button, Space, message, Popconfirm } from 'antd';
import { useNavigate } from 'react-router-dom';
import { accountingService } from '../../services/accountingService';
import type { AccTransactionVO } from '../../types/Accounting';

const TransactionList: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AccTransactionVO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [type, setType] = useState<string | undefined>();

  const load = async (p = page) => {
    setLoading(true);
    try {
      const res = await accountingService.pageTransactions({ pageNo: p, pageSize: 20, type });
      setData(res.records || []);
      setTotal(res.total || 0);
    } catch (e: unknown) {
      const err = e as { message?: string };
      message.error(err.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [type, page]);

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Select
          allowClear
          placeholder="类型"
          style={{ width: 120 }}
          value={type}
          onChange={(v) => { setType(v); setPage(1); }}
          options={[
            { label: '支出', value: 'expense' },
            { label: '收入', value: 'income' },
            { label: '转账', value: 'transfer' },
          ]}
        />
        <Button type="primary" onClick={() => navigate('/accounting/add')}>记一笔</Button>
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        dataSource={data}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }}
        columns={[
          { title: '时间', dataIndex: 'tradeTime', width: 170 },
          { title: '类型', dataIndex: 'type', width: 80 },
          { title: '分类', dataIndex: 'categoryName' },
          { title: '账户', dataIndex: 'accountName' },
          { title: '备注', dataIndex: 'note', ellipsis: true },
          {
            title: '金额',
            dataIndex: 'amount',
            render: (v: number, r: AccTransactionVO) => {
              const color = r.type === 'income' ? '#3f8600' : r.type === 'expense' ? '#cf1322' : '#666';
              const prefix = r.type === 'income' ? '+' : r.type === 'expense' ? '-' : '';
              return <span style={{ color }}>{prefix}{v}</span>;
            },
          },
          {
            title: '操作',
            width: 80,
            render: (_, r) => (
              <Popconfirm title="确认删除？" onConfirm={async () => {
                await accountingService.deleteTransaction(r.id!);
                message.success('已删除');
                load();
              }}>
                <Button type="link" danger size="small">删除</Button>
              </Popconfirm>
            ),
          },
        ]}
      />
    </>
  );
};

export default TransactionList;
