import React, { useEffect, useMemo, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, DatePicker, Radio, message, Spin } from 'antd';
import dayjs from 'dayjs';
import { accountingService } from '../../services/accountingService';
import type { AccCategory, AccChannel, AccTag, AccTransactionVO, FundAccount } from '../../types/Accounting';
import TagSelect from './TagSelect';
import ChannelSelect from './ChannelSelect';

export type TransactionFormType = 'expense' | 'income' | 'transfer';

interface TransactionFormModalProps {
  open: boolean;
  /** 初始类型，默认支出（新建时） */
  defaultType?: TransactionFormType;
  /** 传入则为编辑模式 */
  record?: AccTransactionVO | null;
  onClose: () => void;
  /** 保存成功回调 */
  onSuccess?: () => void;
}

/** 合并下拉选项，编辑时保证当前选中项能显示名称 */
function mergeSelectOptions(
  list: { id?: number; name: string }[],
  selectedId?: number,
  selectedName?: string,
) {
  const opts = list.map((item) => ({ label: item.name, value: Number(item.id) }));
  const id = selectedId != null ? Number(selectedId) : undefined;
  if (id != null && selectedName && !opts.some((o) => o.value === id)) {
    opts.unshift({ label: selectedName, value: id });
  }
  return opts;
}

/**
 * 记一笔 / 编辑流水弹窗。
 */
const TransactionFormModal: React.FC<TransactionFormModalProps> = ({
  open,
  defaultType = 'expense',
  record,
  onClose,
  onSuccess,
}) => {
  const isEdit = Boolean(record?.id);
  const [form] = Form.useForm();
  const [accounts, setAccounts] = useState<FundAccount[]>([]);
  const [categories, setCategories] = useState<AccCategory[]>([]);
  const [channels, setChannels] = useState<AccChannel[]>([]);
  const [tags, setTags] = useState<AccTag[]>([]);
  const [txType, setTxType] = useState<string>(defaultType);
  const [submitting, setSubmitting] = useState(false);
  const [formReady, setFormReady] = useState(false);

  useEffect(() => {
    if (!open) {
      setFormReady(false);
      return;
    }

    let cancelled = false;

    const init = async () => {
      setFormReady(false);
      const [accs, channelList, tagList] = await Promise.all([
        accountingService.listAccounts(),
        accountingService.listChannels(),
        accountingService.listTags(),
      ]);
      if (cancelled) {
        return;
      }
      setAccounts(accs);
      setChannels(channelList);
      setTags(tagList);

      if (record?.id) {
        const type = (record.type as TransactionFormType) || 'expense';
        setTxType(type);
        let cats: AccCategory[] = [];
        if (type !== 'transfer') {
          const catType = type === 'income' ? 'income' : 'expense';
          cats = await accountingService.listCategories(catType);
          if (cancelled) {
            return;
          }
          setCategories(cats);
        } else {
          setCategories([]);
        }
        form.setFieldsValue({
          amount: record.amount,
          accountId: record.accountId != null ? Number(record.accountId) : undefined,
          targetAccountId:
            record.targetAccountId != null ? Number(record.targetAccountId) : undefined,
          categoryId: record.categoryId != null ? Number(record.categoryId) : undefined,
          tradeTime: record.tradeTime ? dayjs(record.tradeTime) : dayjs(),
          payee: record.payee,
          channelId: record.channelId != null ? Number(record.channelId) : undefined,
          tagIds: (record.tagIds ?? []).map((id) => Number(id)),
          note: record.note,
        });
      } else {
        setTxType(defaultType);
        form.resetFields();
        if (defaultType !== 'transfer') {
          const catType = defaultType === 'income' ? 'income' : 'expense';
          const cats = await accountingService.listCategories(catType);
          if (cancelled) {
            return;
          }
          setCategories(cats);
        } else {
          setCategories([]);
        }
        form.setFieldsValue({ tradeTime: dayjs(), amount: undefined, tagIds: [] });
      }
      if (!cancelled) {
        setFormReady(true);
      }
    };

    init();
    return () => {
      cancelled = true;
    };
  }, [open, defaultType, record, form]);

  useEffect(() => {
    if (!open || !formReady || isEdit) {
      return;
    }
    const catType = txType === 'income' ? 'income' : 'expense';
    if (txType === 'transfer') {
      setCategories([]);
      form.setFieldValue('categoryId', undefined);
      return;
    }
    accountingService.listCategories(catType).then(setCategories);
  }, [txType, open, formReady, isEdit, form]);

  const accountOptions = useMemo(
    () =>
      mergeSelectOptions(accounts, record?.accountId, record?.accountName),
    [accounts, record?.accountId, record?.accountName],
  );

  const targetAccountOptions = useMemo(
    () =>
      mergeSelectOptions(accounts, record?.targetAccountId, record?.targetAccountName),
    [accounts, record?.targetAccountId, record?.targetAccountName],
  );

  const categoryOptions = useMemo(
    () =>
      mergeSelectOptions(categories, record?.categoryId, record?.categoryName),
    [categories, record?.categoryId, record?.categoryName],
  );

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const tradeTime = values.tradeTime as { format: (f: string) => string };
      setSubmitting(true);
      const payload = {
        type: txType as TransactionFormType,
        amount: values.amount as number,
        accountId: values.accountId as number,
        targetAccountId: values.targetAccountId as number | undefined,
        categoryId: values.categoryId as number | undefined,
        tradeTime: tradeTime ? tradeTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
        note: values.note as string,
        payee: values.payee as string,
        channelId: values.channelId as number | undefined,
        tagIds: (values.tagIds as number[]) ?? [],
      };
      if (isEdit) {
        await accountingService.updateTransaction({ ...payload, id: record!.id });
        message.success('已更新');
      } else {
        await accountingService.saveTransaction(payload);
        message.success('保存成功');
      }
      onClose();
      onSuccess?.();
    } catch (e: unknown) {
      const err = e as { message?: string; errorFields?: unknown };
      if (!err.errorFields) {
        message.error(err.message || '保存失败');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑流水' : '记一笔'}
      open={open}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={submitting}
      okText="保存"
      cancelText="取消"
      destroyOnClose
      width={480}
    >
      <Spin spinning={!formReady}>
        <Form form={form} layout="vertical">
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
                allowClear
                showSearch
                optionFilterProp="label"
                options={categoryOptions}
                placeholder="选择分类"
              />
            </Form.Item>
          )}
          <Form.Item
            name="accountId"
            label={txType === 'transfer' ? '转出账户' : '账户'}
            rules={[{ required: true, message: '请选择账户' }]}
          >
            <Select
              showSearch
              optionFilterProp="label"
              options={accountOptions}
              placeholder="选择账户"
            />
          </Form.Item>
          {txType === 'transfer' && (
            <Form.Item
              name="targetAccountId"
              label="转入账户"
              rules={[{ required: true, message: '请选择转入账户' }]}
            >
              <Select
                showSearch
                optionFilterProp="label"
                options={targetAccountOptions}
                placeholder="选择账户"
              />
            </Form.Item>
          )}
          <Form.Item name="tradeTime" label="时间">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="payee" label="商户/对方">
            <Input placeholder="可选" />
          </Form.Item>
          <Form.Item name="channelId" label="渠道">
            <ChannelSelect options={channels} />
          </Form.Item>
          <Form.Item name="tagIds" label="标签">
            <TagSelect options={tags} />
          </Form.Item>
          <Form.Item name="note" label="备注">
            <Input.TextArea rows={2} placeholder="可选" />
          </Form.Item>
        </Form>
      </Spin>
    </Modal>
  );
};

export default TransactionFormModal;
