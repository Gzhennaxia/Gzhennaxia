import React, { useEffect, useState } from 'react';
import { Form, Input, InputNumber, Select, DatePicker, Button, Radio, message } from 'antd';
import { useNavigate, useSearchParams } from 'react-router-dom';
import dayjs from 'dayjs';
import { accountingService } from '../../services/accountingService';
import type { AccCategory, FundAccount } from '../../types/Accounting';

const TransactionForm: React.FC = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [search] = useSearchParams();
  const [accounts, setAccounts] = useState<FundAccount[]>([]);
  const [categories, setCategories] = useState<AccCategory[]>([]);
  const [txType, setTxType] = useState<string>(search.get('type') || 'expense');

  useEffect(() => {
    accountingService.listAccounts().then(setAccounts);
  }, []);

  useEffect(() => {
    const catType = txType === 'income' ? 'income' : 'expense';
    if (txType === 'transfer') {
      setCategories([]);
      return;
    }
    accountingService.listCategories(catType).then(setCategories);
  }, [txType]);

  const onFinish = async (values: Record<string, unknown>) => {
    try {
      const tradeTime = values.tradeTime as { format: (f: string) => string };
      await accountingService.saveTransaction({
        type: txType as 'expense' | 'income' | 'transfer',
        amount: values.amount as number,
        accountId: values.accountId as number,
        targetAccountId: values.targetAccountId as number | undefined,
        categoryId: values.categoryId as number | undefined,
        tradeTime: tradeTime ? tradeTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
        note: values.note as string,
        payee: values.payee as string,
      });
      message.success('保存成功');
      navigate('/accounting/transactions');
    } catch (e: unknown) {
      const err = e as { message?: string };
      message.error(err.message || '保存失败');
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      style={{ maxWidth: 480 }}
      initialValues={{ tradeTime: dayjs(), amount: 0 }}
      onFinish={onFinish}
    >
      <Form.Item label="类型">
        <Radio.Group value={txType} onChange={(e) => setTxType(e.target.value)}>
          <Radio.Button value="expense">支出</Radio.Button>
          <Radio.Button value="income">收入</Radio.Button>
          <Radio.Button value="transfer">转账</Radio.Button>
        </Radio.Group>
      </Form.Item>
      <Form.Item name="amount" label="金额" rules={[{ required: true, message: '请输入金额' }]}>
        <InputNumber min={0.01} precision={2} style={{ width: '100%' }} prefix="¥" />
      </Form.Item>
      {txType !== 'transfer' && (
        <Form.Item name="categoryId" label="分类">
          <Select
            options={categories.map((c) => ({ label: c.name, value: c.id }))}
            placeholder="选择分类"
          />
        </Form.Item>
      )}
      <Form.Item name="accountId" label={txType === 'transfer' ? '转出账户' : '账户'} rules={[{ required: true }]}>
        <Select options={accounts.map((a) => ({ label: a.name, value: a.id }))} />
      </Form.Item>
      {txType === 'transfer' && (
        <Form.Item name="targetAccountId" label="转入账户" rules={[{ required: true }]}>
          <Select options={accounts.map((a) => ({ label: a.name, value: a.id }))} />
        </Form.Item>
      )}
      <Form.Item name="tradeTime" label="时间">
        <DatePicker showTime style={{ width: '100%' }} />
      </Form.Item>
      <Form.Item name="note" label="备注">
        <Input.TextArea rows={2} />
      </Form.Item>
      <Button type="primary" htmlType="submit">保存</Button>
      <Button style={{ marginLeft: 8 }} onClick={() => navigate(-1)}>取消</Button>
    </Form>
  );
};

export default TransactionForm;
