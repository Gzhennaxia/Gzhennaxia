import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Button, Switch, message } from 'antd';
import { PlusOutlined, CloseOutlined, MenuOutlined } from '@ant-design/icons';
import { createDict, updateDict, getDictDetail } from '../../../services/dictService';
import { DndContext, DragEndEvent } from '@dnd-kit/core';
import { arrayMove, SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

const { TextArea } = Input;

const SortableItem = ({ id, value, onRemove, onChange }: any) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    display: 'flex',
    marginBottom: 8,
    alignItems: 'center'
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      <Button
        type="text"
        icon={<MenuOutlined />}
        {...listeners}
        style={{ cursor: 'move' }}
      />
      <Input
        style={{ width: 150, marginRight: 8 }}
        placeholder="字典项值"
        value={value.item_key}
        onChange={(e) => onChange(id, 'item_key', e.target.value)}
      />
      <Input
        style={{ width: 150, marginRight: 8 }}
        placeholder="字典项名称"
        value={value.item_value}
        onChange={(e) => onChange(id, 'item_value', e.target.value)}
      />
      <Switch
        checkedChildren="启用"
        unCheckedChildren="禁用"
        checked={value.status === 1}
        onChange={(checked) => onChange(id, 'status', checked ? 1 : 0)}
      />
      <Button
        type="text"
        danger
        icon={<CloseOutlined />}
        onClick={() => onRemove(id)}
      />
    </div>
  );
};

interface DictFormModalProps {
  open: boolean;
  mode?: 'create' | 'edit';
  dictCode?: string;
  onCancel: () => void;
  onSuccess: () => void;
}

const DictFormModal: React.FC<DictFormModalProps> = ({
  open,
  mode = 'create',
  dictCode,
  onCancel,
  onSuccess
}) => {
  const [form] = Form.useForm();
  const [items, setItems] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && mode === 'edit' && dictCode) {
      loadDictData(dictCode);
    } else if (open && mode === 'create') {
      form.resetFields();
      setItems([]);
    }
  }, [open, mode, dictCode]);

  const loadDictData = async (code: string) => {
    try {
      setLoading(true);
      const data = await getDictDetail(code);
      form.setFieldsValue({
        ...data,
        status: data.status === 1
      });
      setItems(data.items?.map(item => ({
        id: item.id || Date.now(),
        item_key: item.item_key,
        item_value: item.item_value,
        status: item.status
      })) || []);
    } catch (error) {
      message.error('获取字典详情失败');
    } finally {
      setLoading(false);
    }
  };

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (active.id !== over?.id) {
      setItems((items) => {
        const oldIndex = items.findIndex(item => item.id === active.id);
        const newIndex = items.findIndex(item => item.id === over?.id);
        return arrayMove(items, oldIndex, newIndex);
      });
    }
  };

  const handleAddItem = () => {
    setItems([...items, { id: Date.now(), item_key: '', item_value: '', status: 1 }]);
  };

  const handleRemoveItem = (id: number) => {
    setItems(items.filter(item => item.id !== id));
  };

  const handleItemChange = (id: number, field: string, value: any) => {
    setItems(items.map(item =>
      item.id === id ? { ...item, [field]: value } : item
    ));
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const dictData = {
        ...values,
        status: values.status ? 1 : 0,
        items: items.map((item, index) => ({
          item_key: item.item_key,
          item_value: item.item_value,
          status: item.status,
          sort: index
        }))
      };

      if (mode === 'create') {
        await createDict(dictData);
        message.success('字典创建成功');
      } else if (mode === 'edit' && dictCode) {
        await updateDict(dictCode, dictData);
        message.success('字典更新成功');
      }

      onSuccess();
      onCancel();
    } catch (error) {
      console.error('Failed to save dict', error);
      message.error(mode === 'create' ? '创建字典失败' : '更新字典失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={mode === 'create' ? '新增字典' : '编辑字典'}
      open={open}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={800}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="code"
          label="字典编码"
          rules={[{ required: true, message: '请输入字典编码' }]}
        >
          <Input placeholder="请输入字典编码" disabled={mode === 'edit'} />
        </Form.Item>
        <Form.Item
          name="name"
          label="字典名称"
          rules={[{ required: true, message: '请输入字典名称' }]}
        >
          <Input placeholder="请输入字典名称" />
        </Form.Item>
        <Form.Item
          name="version"
          label="版本号"
          initialValue="1"
        >
          <Input placeholder="请输入版本号" />
        </Form.Item>
        <Form.Item
          name="status"
          label="状态"
          valuePropName="checked"
          initialValue={true}
        >
          <Switch checkedChildren="启用" unCheckedChildren="禁用" />
        </Form.Item>
        <Form.Item
          name="remark"
          label="备注"
        >
          <TextArea rows={2} placeholder="请输入备注" />
        </Form.Item>

        <div style={{ marginBottom: 16 }}>
          <Button type="dashed" onClick={handleAddItem} icon={<PlusOutlined />}>
            添加字典项
          </Button>
        </div>

        <DndContext onDragEnd={onDragEnd}>
          <SortableContext
            items={items}
            strategy={verticalListSortingStrategy}
          >
            {items.map(item => (
              <SortableItem
                key={item.id}
                id={item.id}
                value={item}
                onRemove={handleRemoveItem}
                onChange={handleItemChange}
              />
            ))}
          </SortableContext>
        </DndContext>
      </Form>
    </Modal>
  );
};

export default DictFormModal;