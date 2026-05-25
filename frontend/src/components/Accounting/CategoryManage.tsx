import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, InputNumber, Select, message, Popconfirm, Space } from 'antd';
import { accountingService } from '../../services/accountingService';
import type { AccCategory } from '../../types/Accounting';

const TYPE_OPTIONS = [
  { label: '支出', value: 'expense' },
  { label: '收入', value: 'income' },
];

/**
 * 收支分类管理。
 */
const CategoryManage: React.FC = () => {
  const [list, setList] = useState<AccCategory[]>([]);
  const [filterType, setFilterType] = useState<string | undefined>();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<AccCategory | null>(null);
  const [form] = Form.useForm();

  const load = () => accountingService.listCategories(filterType).then(setList);

  useEffect(() => {
    load();
  }, [filterType]);

  const openModal = (row?: AccCategory) => {
    setEditing(row ?? null);
    form.setFieldsValue(row ?? { type: filterType ?? 'expense' });
    setOpen(true);
  };

  const onOk = async () => {
    const values = await form.validateFields();
    if (editing?.id) {
      await accountingService.updateCategory({ ...editing, ...values });
    } else {
      await accountingService.saveCategory(values);
    }
    message.success('保存成功');
    setOpen(false);
    load();
  };

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Select
          allowClear
          placeholder="类型筛选"
          style={{ width: 120 }}
          value={filterType}
          onChange={setFilterType}
          options={TYPE_OPTIONS}
        />
        <Button type="primary" onClick={() => openModal()}>
          新增分类
        </Button>
      </Space>
      <Table
        rowKey="id"
        dataSource={list}
        columns={[
          { title: '名称', dataIndex: 'name' },
          {
            title: '类型',
            dataIndex: 'type',
            render: (v: string) => (v === 'income' ? '收入' : '支出'),
          },
          { title: '图标', dataIndex: 'icon', render: (v: string) => v || '-' },
          {
            title: '月预算',
            dataIndex: 'budgetMonthly',
            render: (v: number) => (v != null ? `¥ ${v.toFixed(2)}` : '-'),
          },
          {
            title: '操作',
            width: 140,
            render: (_, r) => (
              <>
                <Button type="link" size="small" onClick={() => openModal(r)}>
                  编辑
                </Button>
                <Popconfirm
                  title="确认删除该分类？"
                  onConfirm={async () => {
                    await accountingService.deleteCategory(r.id!);
                    message.success('已删除');
                    load();
                  }}
                >
                  <Button type="link" danger size="small">
                    删除
                  </Button>
                </Popconfirm>
              </>
            ),
          },
        ]}
      />
      <Modal
        title={editing ? '编辑分类' : '新增分类'}
        open={open}
        onOk={onOk}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input maxLength={50} />
          </Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select options={TYPE_OPTIONS} />
          </Form.Item>
          <Form.Item name="icon" label="图标 key">
            <Input placeholder="可选" />
          </Form.Item>
          <Form.Item name="budgetMonthly" label="月预算（元）">
            <InputNumber min={0} precision={2} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default CategoryManage;
