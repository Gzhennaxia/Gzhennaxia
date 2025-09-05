import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Switch, Button, message } from 'antd';
import { PlusOutlined, DeleteOutlined, DragOutlined } from '@ant-design/icons';
import { DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type { DragEndEvent } from '@dnd-kit/core';
import { createDict, updateDict, getDictDetail } from '../../../services/dictService';

const { TextArea } = Input;

interface DictFormModalProps {
  open: boolean;
  mode?: 'create' | 'edit';
  dictCode?: string;
  onCancel: () => void;
  onSuccess: () => void;
}

interface DictItemFormData {
  id: number;
  itemCode: string;
  itemName: string;
  status: number;
}

interface SortableItemProps {
  id: number;
  value: DictItemFormData;
  onChange: (id: number, field: keyof DictItemFormData, value: any) => void;
  onRemove: (id: number) => void;
}

const SortableItem: React.FC<SortableItemProps> = ({ id, value, onChange, onRemove }) => {
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
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, padding: 8, border: '1px solid #d9d9d9', borderRadius: 4 }}>
        <div {...listeners} style={{ cursor: 'grab' }}>
          <DragOutlined />
        </div>
        <Input
          placeholder="字典项编码"
          value={value.itemCode}
          onChange={(e) => onChange(id, 'itemCode', e.target.value)}
          style={{ flex: 1 }}
        />
        <Input
          placeholder="字典项名称"
          value={value.itemName}
          onChange={(e) => onChange(id, 'itemName', e.target.value)}
          style={{ flex: 1 }}
        />
        <Switch
          checked={value.status === 1}
          onChange={(checked) => onChange(id, 'status', checked ? 1 : 0)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
        <Button
          type="text"
          danger
          icon={<DeleteOutlined />}
          onClick={() => onRemove(id)}
        />
      </div>
    </div>
  );
};

const DictFormModal: React.FC<DictFormModalProps> = ({
  open,
  mode = 'create',
  dictCode,
  onCancel,
  onSuccess
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState<DictItemFormData[]>([]);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  useEffect(() => {
    if (open) {
      if (mode === 'create') {
        form.resetFields();
        setItems([]);
      } else if (mode === 'edit' && dictCode) {
        loadDictData(dictCode);
      }
    }
  }, [open, mode, dictCode, form]);

  const loadDictData = async (code: string) => {
    try {
      setLoading(true);
      const data = await getDictDetail(code);
      form.setFieldsValue({
        dictCode: data.dictCode,
        dictName: data.dictName,
        status: data.status === 1,
        remark: data.remark
      });
      setItems(data.items?.map(item => ({
        id: item.id || Date.now(),
        itemCode: item.itemCode,
        itemName: item.itemName,
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

  const handleItemChange = (id: number, field: keyof DictItemFormData, value: any) => {
    setItems(items.map(item =>
      item.id === id ? { ...item, [field]: value } : item
    ));
  };

  const handleRemoveItem = (id: number) => {
    setItems(items.filter(item => item.id !== id));
  };

  const handleAddItem = () => {
    setItems([...items, { id: Date.now(), itemCode: '', itemName: '', status: 1 }]);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const dictData = {
        dictCode: values.dictCode,
        dictName: values.dictName,
        status: values.status ? 1 : 0,
        remark: values.remark,
        items: items.map((item, index) => ({
          id: item.id,
          dictCode: values.dictCode,
          itemCode: item.itemCode,
          itemName: item.itemName,
          status: item.status,
          sort: index,
          createdTime: new Date().toISOString(),
          updatedTime: new Date().toISOString()
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
          name="dictCode"
          label="字典编码"
          rules={[{ required: true, message: '请输入字典编码' }]}
        >
          <Input placeholder="请输入字典编码" disabled={mode === 'edit'} />
        </Form.Item>
        <Form.Item
          name="dictName"
          label="字典名称"
          rules={[{ required: true, message: '请输入字典名称' }]}
        >
          <Input placeholder="请输入字典名称" />
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
          <TextArea rows={3} placeholder="请输入备注" />
        </Form.Item>

        <div style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
            <h4 style={{ margin: 0 }}>字典项列表</h4>
            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={handleAddItem}
            >
              添加字典项
            </Button>
          </div>

          <DndContext
            sensors={sensors}
            collisionDetection={closestCenter}
            onDragEnd={onDragEnd}
          >
            <SortableContext items={items.map(item => item.id)} strategy={verticalListSortingStrategy}>
              {items.map((item) => (
                <SortableItem
                  key={item.id}
                  id={item.id}
                  value={item}
                  onChange={handleItemChange}
                  onRemove={handleRemoveItem}
                />
              ))}
            </SortableContext>
          </DndContext>

          {items.length === 0 && (
            <div style={{ textAlign: 'center', padding: 20, color: '#999' }}>
              暂无字典项，点击上方按钮添加
            </div>
          )}
        </div>
      </Form>
    </Modal>
  );
};

export default DictFormModal;