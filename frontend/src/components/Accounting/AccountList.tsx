import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, InputNumber, Select, Switch, message, Popconfirm } from 'antd';
import { accountingService } from '../../services/accountingService';
import type { FundAccount } from '../../types/Accounting';

const ACCOUNT_TYPES = [
  { label: '银行卡', value: 'bank' },
  { label: '支付宝', value: 'alipay' },
  { label: '微信', value: 'wechat' },
  { label: '现金', value: 'cash' },
  { label: '信用卡', value: 'credit' },
  { label: '虚拟账户', value: 'virtual' },
  { label: '其他', value: 'other' },
];

const AccountList: React.FC = () => {
  const [list, setList] = useState<FundAccount[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<FundAccount | null>(null);
  const [form] = Form.useForm();

  const load = () => accountingService.listAccounts().then(setList);

  useEffect(() => { load(); }, []);

  const openModal = (row?: FundAccount) => {
    setEditing(row || null);
    form.setFieldsValue(row || { type: 'bank', currency: 'CNY', includeInNetWorth: 1, balance: 0 });
    setOpen(true);
  };

  const onOk = async () => {
    const values = await form.validateFields();
    const payload = { ...values, includeInNetWorth: values.includeInNetWorth ? 1 : 0 };
    if (editing?.id) {
      await accountingService.updateAccount({ ...editing, ...payload });
    } else {
      await accountingService.saveAccount(payload);
    }
    message.success('保存成功');
    setOpen(false);
    load();
  };

  return (
    <>
      <Button type="primary" style={{ marginBottom: 16 }} onClick={() => openModal()}>新增账户</Button>
      <Table
        rowKey="id"
        dataSource={list}
        columns={[
          { title: '名称', dataIndex: 'name' },
          { title: '编号', dataIndex: 'accountNo', render: (v: string) => v ? `****${v.slice(-4)}` : '-' },
          { title: '类型', dataIndex: 'type' },
          { title: '余额', dataIndex: 'balance', render: (v: number) => `¥ ${(v ?? 0).toFixed(2)}` },
          {
            title: '计入净资产',
            dataIndex: 'includeInNetWorth',
            render: (v: number) => (v === 1 ? '是' : '否'),
          },
          {
            title: '操作',
            render: (_, r) => (
              <>
                <Button type="link" onClick={() => openModal(r)}>编辑</Button>
                <Popconfirm title="确认删除？" onConfirm={async () => {
                  await accountingService.deleteAccount(r.id!);
                  load();
                }}>
                  <Button type="link" danger>删除</Button>
                </Popconfirm>
              </>
            ),
          },
        ]}
      />
      <Modal title={editing ? '编辑账户' : '新增账户'} open={open} onOk={onOk} onCancel={() => setOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="accountNo" label="账户编号"><Input placeholder="卡号、支付宝 ID 等" /></Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true }]}><Select options={ACCOUNT_TYPES} /></Form.Item>
          <Form.Item name="balance" label="余额"><InputNumber style={{ width: '100%' }} precision={2} /></Form.Item>
          <Form.Item name="includeInNetWorth" label="计入净资产" valuePropName="checked">
            <Switch defaultChecked />
          </Form.Item>
          <Form.Item name="remark" label="备注"><Input.TextArea /></Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default AccountList;
