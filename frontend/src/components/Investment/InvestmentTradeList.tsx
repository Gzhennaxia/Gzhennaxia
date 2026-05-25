import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Select, InputNumber, DatePicker, Input, message, Popconfirm } from 'antd';
import dayjs from 'dayjs';
import { investmentService } from '../../services/investmentService';
import type { InvestmentSymbol, InvestmentTrade } from '../../types/Accounting';

const TRADE_TYPES = [
  { label: '买入', value: 'buy' },
  { label: '卖出', value: 'sell' },
  { label: '分红', value: 'div' + 'idend' },
  { label: '申购', value: 'subscribe' },
  { label: '赎回', value: 'redeem' },
];

const InvestmentTradeList: React.FC = () => {
  const [trades, setTrades] = useState<InvestmentTrade[]>([]);
  const [symbols, setSymbols] = useState<InvestmentSymbol[]>([]);
  const [open, setOpen] = useState(false);
  const [symbolOpen, setSymbolOpen] = useState(false);
  const [form] = Form.useForm();
  const [symbolForm] = Form.useForm();

  const load = async () => {
    const [t, s] = await Promise.all([
      investmentService.listInvestmentTrades(),
      investmentService.listSymbols(),
    ]);
    setTrades(t);
    setSymbols(s);
  };

  useEffect(() => {
    load();
  }, []);

  const saveTrade = async () => {
    const v = await form.validateFields();
    const tradeTime = v.tradeTime as { format: (f: string) => string };
    await investmentService.saveInvestmentTrade({
      symbolId: v.symbolId,
      tradeType: v.tradeType,
      quantity: v.quantity,
      price: v.price,
      amount: v.amount,
      fee: v.fee ?? 0,
      tradeTime: tradeTime ? tradeTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
      note: v.note,
    });
    message.success('已保存');
    setOpen(false);
    load();
  };

  const saveSymbol = async () => {
    const v = await symbolForm.validateFields();
    await investmentService.saveSymbol(v);
    message.success('标的已添加');
    setSymbolOpen(false);
    load();
  };

  const symbolMap = Object.fromEntries(symbols.map((s) => [s.id, s.name]));

  return (
    <>
      <Button
        type="primary"
        style={{ marginRight: 8 }}
        onClick={() => {
          form.resetFields();
          form.setFieldsValue({ tradeTime: dayjs() });
          setOpen(true);
        }}
      >
        录入交易
      </Button>
      <Button
        onClick={() => {
          symbolForm.resetFields();
          setSymbolOpen(true);
        }}
      >
        新增标的
      </Button>
      <Table
        style={{ marginTop: 16 }}
        rowKey="id"
        dataSource={trades}
        columns={[
          { title: '时间', dataIndex: 'tradeTime' },
          { title: '类型', dataIndex: 'tradeType' },
          { title: '标的', dataIndex: 'symbolId', render: (id: number) => symbolMap[id] ?? id },
          { title: '数量', dataIndex: 'quantity' },
          { title: '金额', dataIndex: 'amount' },
          {
            title: '操作',
            render: (_, r) => (
              <Popconfirm
                title="确认删除？"
                onConfirm={async () => {
                  await investmentService.deleteInvestmentTrade(r.id!);
                  load();
                }}
              >
                <Button type="link" danger size="small">
                  删除
                </Button>
              </Popconfirm>
            ),
          },
        ]}
      />
      <Modal title="录入投资交易" open={open} onOk={saveTrade} onCancel={() => setOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="symbolId" label="标的" rules={[{ required: true }]}>
            <Select options={symbols.map((s) => ({ label: `${s.name} (${s.symbol})`, value: s.id }))} />
          </Form.Item>
          <Form.Item name="tradeType" label="类型" rules={[{ required: true }]}>
            <Select options={TRADE_TYPES} />
          </Form.Item>
          <Form.Item name="quantity" label="数量">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="price" label="价格">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="amount" label="成交金额" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} precision={2} />
          </Form.Item>
          <Form.Item name="tradeTime" label="时间">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="note" label="备注">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      <Modal title="新增标的" open={symbolOpen} onOk={saveSymbol} onCancel={() => setSymbolOpen(false)}>
        <Form form={symbolForm} layout="vertical">
          <Form.Item name="symbol" label="代码" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="market" label="市场">
            <Input placeholder="A股/港股/美股" />
          </Form.Item>
          <Form.Item name="currentPrice" label="现价">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default InvestmentTradeList;
