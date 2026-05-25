import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, message, Popconfirm, Tag } from 'antd';
import { accountingService } from '../../services/accountingService';
import type { AccTag } from '../../types/Accounting';
import TagColorPicker from './TagColorPicker';
import { DEFAULT_TAG_COLOR, toAntTagColor } from '../../constants/tagColors';

/**
 * 流水标签管理。
 */
const TagManage: React.FC = () => {
  const [list, setList] = useState<AccTag[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<AccTag | null>(null);
  const [form] = Form.useForm();

  const load = () => accountingService.listTags().then(setList);

  useEffect(() => {
    load();
  }, []);

  const openModal = (row?: AccTag) => {
    setEditing(row ?? null);
    form.setFieldsValue(
      row
        ? { name: row.name, color: row.color || DEFAULT_TAG_COLOR }
        : { color: DEFAULT_TAG_COLOR },
    );
    setOpen(true);
  };

  const onOk = async () => {
    const values = await form.validateFields();
    if (editing?.id) {
      await accountingService.updateTag({
        ...editing,
        name: values.name as string,
        color: values.color as string,
      });
    } else {
      await accountingService.saveTag({
        name: values.name as string,
        color: values.color as string,
      });
    }
    message.success('保存成功');
    setOpen(false);
    load();
  };

  return (
    <>
      <Button type="primary" style={{ marginBottom: 16 }} onClick={() => openModal()}>
        新增标签
      </Button>
      <Table
        rowKey="id"
        dataSource={list}
        columns={[
          {
            title: '名称',
            dataIndex: 'name',
            render: (name: string, r) => (
              <Tag color={toAntTagColor(r.color)}>{name}</Tag>
            ),
          },
          {
            title: '颜色',
            dataIndex: 'color',
            width: 80,
            render: (v: string) => {
              const preset = toAntTagColor(v);
              return <Tag color={preset}>{v || DEFAULT_TAG_COLOR}</Tag>;
            },
          },
          {
            title: '流水数',
            dataIndex: 'transactionCount',
            width: 90,
            align: 'right' as const,
            render: (n: number | undefined) => n ?? 0,
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
                  title="确认删除该标签？"
                  onConfirm={async () => {
                    await accountingService.deleteTag(r.id!);
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
        title={editing ? '编辑标签' : '新增标签'}
        open={open}
        onOk={onOk}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="名称"
            rules={[{ required: true, message: '请输入名称' }]}
          >
            <Input maxLength={50} placeholder="标签名称" />
          </Form.Item>
          <Form.Item
            name="color"
            label="颜色"
            rules={[{ required: true, message: '请选择颜色' }]}
          >
            <TagColorPicker />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default TagManage;
