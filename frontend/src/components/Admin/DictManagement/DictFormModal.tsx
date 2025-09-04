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
    isDragging,
  } = useSortable({ id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    display: 'flex',
    alignItems: 'center',
    padding: '12px 16px',
    backgroundColor: isDragging ? '#f0f8ff' : '#fff',
    opacity: isDragging ? 0.8 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      {/* 拖拽手柄 */}
      <div 
        {...listeners}
        style={{ 
          width: '40px', 
          cursor: 'move',
          display: 'flex',
          justifyContent: 'center',
          color: '#999'
        }}
      >
        <MenuOutlined />
      </div>
      
      {/* 字典项值 */}
      <div style={{ flex: 1, marginRight: 16 }}>
        <Input
          placeholder="字典项值"
          value={value.item_code}
          onChange={(e) => onChange(id, 'item_code', e.target.value)}
          bordered={false}
          style={{ padding: '4px 0' }}
        />
      </div>
      
      {/* 字典项名称 */}
      <div style={{ flex: 1, marginRight: 16 }}>
        <Input
          placeholder="字典项名称"
          value={value.item_name}
          onChange={(e) => onChange(id, 'item_name', e.target.value)}
          bordered={false}
          style={{ padding: '4px 0' }}
        />
      </div>
      
      {/* 状态开关 */}
      <div style={{ width: '80px', marginRight: 16 }}>
        <Switch
          size="small"
          checkedChildren="启用"
          unCheckedChildren="禁用"
          checked={value.status === 1}
          onChange={(checked) => onChange(id, 'status', checked ? 1 : 0)}
        />
      </div>
      
      {/* 删除按钮 */}
      <div style={{ width: '60px', display: 'flex', justifyContent: 'center' }}>
        <Button
          type="text"
          danger
          size="small"
          icon={<CloseOutlined />}
          onClick={() => onRemove(id)}
          style={{ padding: '4px' }}
        />
      </div>
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
        item_code: item.item_code,
        item_name: item.item_name,
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
    setItems([...items, { id: Date.now(), item_code: '', item_name: '', status: 1 }]);
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
          item_code: item.item_code,
          item_name: item.item_name,
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
          <h4>字典项列表</h4>
        </div>

        <div style={{ 
          border: '1px solid #d9d9d9', 
          borderRadius: '6px',
          overflow: 'hidden'
        }}>
          {/* 表头 */}
          <div style={{
            display: 'flex',
            backgroundColor: '#fafafa',
            padding: '12px 16px',
            borderBottom: '1px solid #d9d9d9',
            fontWeight: 500
          }}>
            <div style={{ width: '40px' }}></div>
            <div style={{ flex: 1, marginRight: 16 }}>字典项编码</div>
            <div style={{ flex: 1, marginRight: 16 }}>字典项名称</div>
            <div style={{ width: '80px', marginRight: 16 }}>状态</div>
            <div style={{ width: '60px' }}>操作</div>
          </div>

          {/* 字典项列表 */}
          <DndContext onDragEnd={onDragEnd}>
            <SortableContext
              items={items}
              strategy={verticalListSortingStrategy}
            >
              {items.map((item, index) => (
                <div key={item.id} style={{
                  borderBottom: index < items.length - 1 ? '1px solid #f0f0f0' : 'none'
                }}>
                  <SortableItem
                    id={item.id}
                    value={item}
                    onRemove={handleRemoveItem}
                    onChange={handleItemChange}
                  />
                </div>
              ))}
            </SortableContext>
          </DndContext>

          {/* 新增按钮行 */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            padding: '12px 16px',
            borderTop: items.length > 0 ? '1px solid #f0f0f0' : 'none',
            backgroundColor: '#fafafa'
          }}>
            <Button 
              type="dashed" 
              onClick={handleAddItem} 
              icon={<PlusOutlined />}
              style={{ width: '100%' }}
            >
              添加字典项
            </Button>
          </div>
        </div>
      </Form>
    </Modal>
  );
};

export default DictFormModal;