import React, { useState, useEffect } from 'react';
import { Card, List, Button, Input, Switch, Space, message, Popconfirm } from 'antd';
import { DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { PlusOutlined, DeleteOutlined, HolderOutlined } from '@ant-design/icons';
import { DictItem } from '../../../types/dict';

interface DraggableDictItemListProps {
  items: DictItem[];
  onChange: (items: DictItem[]) => void;
  disabled?: boolean;
}

interface SortableItemProps {
  item: DictItem;
  onUpdate: (item: DictItem) => void;
  onDelete: (id: number) => void;
  disabled?: boolean;
}

const SortableItem: React.FC<SortableItemProps> = ({ item, onUpdate, onDelete, disabled }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: item.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  const handleKeyChange = (value: string) => {
    onUpdate({ ...item, itemCode: value });
  };

  const handleValueChange = (value: string) => {
    onUpdate({ ...item, itemName: value });
  };

  const handleStatusChange = (checked: boolean) => {
    onUpdate({ ...item, status: checked ? 1 : 0 });
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      <List.Item
        style={{
          padding: '12px 16px',
          border: '1px solid #f0f0f0',
          borderRadius: '6px',
          marginBottom: '8px',
          backgroundColor: '#fff',
          cursor: disabled ? 'default' : 'move',
        }}
      >
        <Space style={{ width: '100%' }} align="start">
          <div {...listeners} style={{ cursor: disabled ? 'default' : 'grab', padding: '4px' }}>
            <HolderOutlined style={{ color: disabled ? '#d9d9d9' : '#999' }} />
          </div>
          
          <div style={{ flex: 1 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Space style={{ width: '100%' }}>
                <div style={{ width: '120px' }}>
                  <div style={{ marginBottom: '4px', fontSize: '12px', color: '#666' }}>标签</div>
                  <Input
                    value={item.itemCode}
                    onChange={(e) => handleKeyChange(e.target.value)}
                    placeholder="请输入标签"
                    size="small"
                    disabled={disabled}
                  />
                </div>
                <div style={{ width: '120px' }}>
                  <div style={{ marginBottom: '4px', fontSize: '12px', color: '#666' }}>值</div>
                  <Input
                    value={item.itemName}
                    onChange={(e) => handleValueChange(e.target.value)}
                    placeholder="请输入值"
                    size="small"
                    disabled={disabled}
                  />
                </div>
                <div style={{ width: '80px' }}>
                  <div style={{ marginBottom: '4px', fontSize: '12px', color: '#666' }}>状态</div>
                  <Switch
                    checked={item.status === 1}
                    onChange={handleStatusChange}
                    size="small"
                    disabled={disabled}
                  />
                </div>
                <div style={{ width: '60px', paddingTop: '20px' }}>
                  <Popconfirm
                    title="确定删除这个字典项吗？"
                    onConfirm={() => onDelete(item.id)}
                    okText="确定"
                    cancelText="取消"
                    disabled={disabled}
                  >
                    <Button
                      type="text"
                      danger
                      icon={<DeleteOutlined />}
                      size="small"
                      disabled={disabled}
                    />
                  </Popconfirm>
                </div>
              </Space>
            </Space>
          </div>
        </Space>
      </List.Item>
    </div>
  );
};

const DraggableDictItemList: React.FC<DraggableDictItemListProps> = ({ 
  items, 
  onChange, 
  disabled = false 
}) => {
  const [localItems, setLocalItems] = useState<DictItem[]>(items);
  const [nextId, setNextId] = useState<number>(Math.max(...items.map(item => item.id), 0) + 1);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  useEffect(() => {
    setLocalItems(items);
    if (items.length > 0) {
      setNextId(Math.max(...items.map(item => item.id), 0) + 1);
    }
  }, [items]);

  const handleDragEnd = (event: any) => {
    const { active, over } = event;

    if (active.id !== over.id) {
      const oldIndex = localItems.findIndex(item => item.id === active.id);
      const newIndex = localItems.findIndex(item => item.id === over.id);
      
      const newItems = arrayMove(localItems, oldIndex, newIndex).map((item, index) => ({
        ...item,
        sort: index + 1,
      }));
      
      setLocalItems(newItems);
      onChange(newItems);
    }
  };

  const handleAddItem = () => {
    const newItem: DictItem = {
      id: nextId,
      dictCode: '',
      itemCode: '',
      itemName: '',
      sort: localItems.length + 1,
      status: 1,
      createdTime: new Date().toISOString(),
      updatedTime: new Date().toISOString(),
    };

    const newItems = [...localItems, newItem];
    setLocalItems(newItems);
    onChange(newItems);
    setNextId(nextId + 1);
  };

  const handleUpdateItem = (updatedItem: DictItem) => {
    const newItems = localItems.map(item => 
      item.id === updatedItem.id ? { ...updatedItem, updated_time: new Date().toISOString() } : item
    );
    setLocalItems(newItems);
    onChange(newItems);
  };

  const handleDeleteItem = (id: number) => {
    const newItems = localItems
      .filter(item => item.id !== id)
      .map((item, index) => ({ ...item, sort: index + 1 }));
    
    setLocalItems(newItems);
    onChange(newItems);
  };

  const validateItems = () => {
    const errors: string[] = [];
    const values = new Set<string>();
    
    localItems.forEach((item, index) => {
      if (!item.itemCode.trim()) {
        errors.push(`第${index + 1}行：标签不能为空`);
      }
      if (!item.itemName.trim()) {
        errors.push(`第${index + 1}行：值不能为空`);
      }
      if (values.has(item.itemName)) {
        errors.push(`第${index + 1}行：值"${item.itemName}"重复`);
      } else {
        values.add(item.itemName);
      }
    });

    if (errors.length > 0) {
      message.error(errors.join('\n'));
      return false;
    }
    return true;
  };

  return (
    <Card
      title="字典项配置"
      extra={
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleAddItem}
          disabled={disabled}
        >
          新增字典项
        </Button>
      }
    >
      {localItems.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
          暂无字典项，点击"新增字典项"按钮添加
        </div>
      ) : (
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragEnd={handleDragEnd}
        >
          <SortableContext items={localItems.map(item => item.id)} strategy={verticalListSortingStrategy}>
            <List
              dataSource={localItems}
              renderItem={(item) => (
                <SortableItem
                  key={item.id}
                  item={item}
                  onUpdate={handleUpdateItem}
                  onDelete={handleDeleteItem}
                  disabled={disabled}
                />
              )}
            />
          </SortableContext>
        </DndContext>
      )}
    </Card>
  );
};

export default DraggableDictItemList;