import React, { useEffect, useState } from 'react';
import { Table, Select, Button, Space, message, Popconfirm, Tag } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { formatTxType } from '../../constants/accounting';
import { useAccountingActions } from './AccountingActionsContext';
import { accountingService } from '../../services/accountingService';
import type { AccTransactionVO } from '../../types/Accounting';
import TransactionImportModal from './TransactionImportModal';
import TransactionFormModal from './TransactionFormModal';

const TransactionList: React.FC = () => {
  const { openAddTransaction } = useAccountingActions();
  const [importOpen, setImportOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AccTransactionVO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [type, setType] = useState<string | undefined>();
  const [editOpen, setEditOpen] = useState(false);
  const [editing, setEditing] = useState<AccTransactionVO | null>(null);

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
        <Button icon={<UploadOutlined />} onClick={() => setImportOpen(true)}>
          批量导入
        </Button>
        <Button type="primary" onClick={() => openAddTransaction()}>记一笔</Button>
      </Space>
      <TransactionImportModal
        open={importOpen}
        onClose={() => setImportOpen(false)}
        onSuccess={() => {
          setPage(1);
          load(1);
        }}
      />
      <TransactionFormModal
        open={editOpen}
        record={editing}
        onClose={() => {
          setEditOpen(false);
          setEditing(null);
        }}
        onSuccess={() => {
          setEditOpen(false);
          setEditing(null);
          load();
        }}
      />
      <Table
        rowKey="id"
        loading={loading}
        dataSource={data}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }}
        columns={[
          { title: '时间', dataIndex: 'tradeTime', width: 170 },
          {
            title: '类型',
            dataIndex: 'type',
            width: 80,
            render: (type: string) => formatTxType(type),
          },
          { title: '分类', dataIndex: 'categoryName' },
          { title: '渠道', dataIndex: 'channelName', width: 90, render: (v: string) => v || '-' },
          {
            title: '标签',
            dataIndex: 'tagNames',
            render: (names: string[] | undefined) =>
              names?.length ? names.map((n) => <Tag key={n}>{n}</Tag>) : '-',
          },
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
            width: 120,
            render: (_, r) => (
              <Space size={0}>
                <Button
                  type="link"
                  size="small"
                  onClick={() => {
                    setEditing(r);
                    setEditOpen(true);
                  }}
                >
                  编辑
                </Button>
                <Popconfirm
                  title="确认删除？"
                  onConfirm={async () => {
                    await accountingService.deleteTransaction(r.id!);
                    message.success('已删除');
                    load();
                  }}
                >
                  <Button type="link" danger size="small">删除</Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />
    </>
  );
};

export default TransactionList;
