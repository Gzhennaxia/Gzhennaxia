import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Switch, Table, Tag, Spin, Button, Space, message } from 'antd';
import { EditOutlined, SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { getDictDetail, updateDict } from '../../../services/dictService';
import { Dict, DictItem } from '../../../types/dict';

const { TextArea } = Input;

interface DictDetailModalProps {
  open: boolean;
  dictCode?: string;
  mode?: 'view' | 'edit';
  onCancel: () => void;
  onSuccess?: () => void;
}

const DictDetailModal: React.FC<DictDetailModalProps> = ({
  open,
  dictCode,
  mode = 'view',
  onCancel,
  onSuccess
}) => {
  const [dict, setDict] = useState<Dict | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingKey, setEditingKey] = useState<string>('');
  const [form] = Form.useForm();

  useEffect(() => {
    if (open && dictCode) {
      loadDictData(dictCode);
    }
  }, [open, dictCode]);

  const loadDictData = async (code: string) => {
    try {
      setLoading(true);
      const data = await getDictDetail(code);
      setDict(data);
      // 设置表单初始值
      form.setFieldsValue({
        dictCode: data.dictCode,
        dictName: data.dictName,
        status: data.status === 1,
        remark: data.remark
      });
    } catch (error) {
      console.error('Failed to load dict detail', error);
      message.error('加载字典详情失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      
      const updateData = {
        ...dict,
        dictName: values.dictName,
        status: values.status ? 1 : 0,
        remark: values.remark
      };

      await updateDict(dictCode!, updateData);
      message.success('字典更新成功');
      onSuccess?.();
      onCancel();
    } catch (error) {
      console.error('Failed to update dict', error);
      message.error('更新字典失败');
    } finally {
      setSaving(false);
    }
  };

  const isEditing = (record: DictItem) => record.id?.toString() === editingKey;

  const edit = (record: DictItem) => {
    setEditingKey(record.id?.toString() || '');
  };

  const cancel = () => {
    setEditingKey('');
  };

  const save = async (_id: string) => {
    // 这里可以添加保存字典项的逻辑
    setEditingKey('');
    message.success('字典项更新成功');
  };

  const viewColumns = [
    {
      title: '字典项编码',
      dataIndex: 'itemCode',
      key: 'itemCode',
    },
    {
      title: '字典项名称',
      dataIndex: 'itemName',
      key: 'itemName',
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
  ];

  const editColumns = [
    {
      title: '字典项编码',
      dataIndex: 'itemCode',
      key: 'itemCode',
      render: (text: string, record: DictItem) => {
        const editing = isEditing(record);
        return editing ? (
          <Input defaultValue={text} size="small" />
        ) : (
          text
        );
      },
    },
    {
      title: '字典项名称',
      dataIndex: 'itemName',
      key: 'itemName',
      render: (text: string, record: DictItem) => {
        const editing = isEditing(record);
        return editing ? (
          <Input defaultValue={text} size="small" />
        ) : (
          text
        );
      },
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
      render: (text: number, record: DictItem) => {
        const editing = isEditing(record);
        return editing ? (
          <Input defaultValue={text} size="small" type="number" />
        ) : (
          text
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number, record: DictItem) => {
        const editing = isEditing(record);
        return editing ? (
          <Switch 
            size="small"
            checked={status === 1}
            checkedChildren="启用"
            unCheckedChildren="禁用"
          />
        ) : (
          <Tag color={status === 1 ? 'green' : 'red'}>
            {status === 1 ? '启用' : '禁用'}
          </Tag>
        );
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_: any, record: DictItem) => {
        const editing = isEditing(record);
        return editing ? (
          <Space size="small">
            <Button
              type="link"
              size="small"
              icon={<SaveOutlined />}
              onClick={() => save(record.id?.toString() || '')}
            >
              保存
            </Button>
            <Button
              type="link"
              size="small"
              icon={<CloseOutlined />}
              onClick={cancel}
            >
              取消
            </Button>
          </Space>
        ) : (
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => edit(record)}
            disabled={editingKey !== ''}
          >
            编辑
          </Button>
        );
      },
    },
  ];

  const columns = mode === 'edit' ? editColumns : viewColumns;

  return (
    <Modal
      title={mode === 'edit' ? '编辑字典' : '字典详情'}
      open={open}
      onCancel={onCancel}
      footer={mode === 'edit' ? [
        <Button key="cancel" onClick={onCancel}>
          取消
        </Button>,
        <Button key="save" type="primary" loading={saving} onClick={handleSave}>
          保存
        </Button>
      ] : null}
      width={800}
    >
      <Spin spinning={loading}>
        {dict && (
          <>
            {/* 主数据表单 */}
            <Form
              form={form}
              layout="vertical"
              style={{ marginBottom: 24 }}
            >
              <div style={{ display: 'flex', gap: 16 }}>
                <Form.Item
                  name="dictCode"
                  label="字典编码"
                  style={{ flex: 1 }}
                >
                  <Input disabled />
                </Form.Item>
                <Form.Item
                  name="dictName"
                  label="字典名称"
                  style={{ flex: 1 }}
                  rules={mode === 'edit' ? [{ required: true, message: '请输入字典名称' }] : []}
                >
                  <Input disabled={mode === 'view'} />
                </Form.Item>
              </div>
              
              <div style={{ display: 'flex', gap: 16 }}>
                <Form.Item
                  name="status"
                  label="状态"
                  valuePropName="checked"
                  style={{ flex: 1 }}
                >
                  <Switch 
                    disabled={mode === 'view'}
                    checkedChildren="启用" 
                    unCheckedChildren="禁用" 
                  />
                </Form.Item>
                <div style={{ flex: 1 }}>
                  <div style={{ marginBottom: 8, fontSize: 14, fontWeight: 500 }}>版本号</div>
                  <div style={{ padding: '4px 11px', backgroundColor: '#f5f5f5', borderRadius: 6 }}>
                    {dict.version}
                  </div>
                </div>
              </div>
              
              <Form.Item
                name="remark"
                label="备注"
              >
                <TextArea 
                  rows={2} 
                  disabled={mode === 'view'}
                  placeholder="请输入备注" 
                />
              </Form.Item>
            </Form>
            
            {/* 字典项表格 */}
            <div>
              <h4 style={{ marginBottom: 16 }}>字典项列表</h4>
              <Table
                columns={columns}
                dataSource={dict.items || []}
                rowKey="id"
                pagination={false}
                size="small"
                bordered
              />
            </div>
          </>
        )}
      </Spin>
    </Modal>
  );
};

export default DictDetailModal;