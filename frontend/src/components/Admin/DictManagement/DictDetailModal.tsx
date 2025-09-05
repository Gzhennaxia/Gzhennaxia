import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Switch, Table, Button, Space, message, Popconfirm } from 'antd';
import { PlusOutlined, DeleteOutlined, DragOutlined, SaveOutlined, EditOutlined, CloseOutlined } from '@ant-design/icons';
import { DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type { DragEndEvent } from '@dnd-kit/core';
import { getDictDetail, createDict, updateDict } from '../../../services/dictService';
import { Dict, DictItem } from '../../../types/dict';

const { TextArea } = Input;

interface DictDetailModalProps {
  open: boolean;
  dictCode?: string;
  mode: 'create' | 'edit' | 'view';
  onCancel: () => void;
  onSuccess?: () => void;
}

interface DictItemFormData {
  id: number | string;
  itemCode: string;
  itemName: string;
  status: number;
  sort: number;
  isNew?: boolean;
  isEditing?: boolean;
}

interface SortableRowProps {
  children: React.ReactNode;
  'data-row-key': string;
}

const SortableRow: React.FC<SortableRowProps> = ({ children, ...props }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: props['data-row-key'],
  });

  const style: React.CSSProperties = {
    ...props.style,
    transform: CSS.Transform.toString(transform && { ...transform, scaleY: 1 }),
    transition,
    ...(isDragging ? { position: 'relative', zIndex: 9999 } : {}),
  };

  return (
    <tr {...props} ref={setNodeRef} style={style} {...attributes}>
      {React.Children.map(children, (child) => {
        if ((child as React.ReactElement).key === 'sort') {
          return React.cloneElement(child as React.ReactElement, {
            children: (
              <div
                {...listeners}
                style={{
                  cursor: 'grab',
                  padding: '4px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <DragOutlined />
              </div>
            ),
          });
        }
        return child;
      })}
    </tr>
  );
};

const DictDetailModal: React.FC<DictDetailModalProps> = ({
  open,
  dictCode,
  mode,
  onCancel,
  onSuccess
}) => {
  const [dict, setDict] = useState<Dict | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [items, setItems] = useState<DictItemFormData[]>([]);
  const [form] = Form.useForm();

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 1,
      },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  useEffect(() => {
    if (open) {
      if (mode === 'create') {
        form.resetFields();
        form.setFieldsValue({
          status: true,
        });
        setItems([]);
        setDict(null);
      } else if ((mode === 'edit' || mode === 'view') && dictCode) {
        loadDictData(dictCode);
      }
    }
  }, [open, mode, dictCode, form]);

  const loadDictData = async (code: string) => {
    try {
      setLoading(true);
      const data = await getDictDetail(code);
      setDict(data);
      form.setFieldsValue({
        dictCode: data.dictCode,
        dictName: data.dictName,
        status: data.status === 1,
        remark: data.remark
      });
      
      const formattedItems = (data.items || []).map((item, index) => ({
        id: item.id || `item-${index}`,
        itemCode: item.itemCode,
        itemName: item.itemName,
        status: item.status,
        sort: item.sort || index,
        isNew: false,
        isEditing: false,
      }));
      
      setItems(formattedItems);
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
      
      const validItems = items.filter(item => item.itemCode && item.itemName);
      
      const dictData = {
        dictCode: values.dictCode,
        dictName: values.dictName,
        status: values.status ? 1 : 0,
        remark: values.remark,
        items: validItems.map((item, index) => ({
          id: typeof item.id === 'string' && item.id.startsWith('temp-') ? undefined : item.id,
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
      } else if (mode === 'edit') {
        await updateDict(dictCode!, dictData);
        message.success('字典更新成功');
      }

      onSuccess?.();
      onCancel();
    } catch (error) {
      console.error('Failed to save dict', error);
      message.error(mode === 'create' ? '创建字典失败' : '更新字典失败');
    } finally {
      setSaving(false);
    }
  };

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (active.id !== over?.id) {
      setItems((items) => {
        const oldIndex = items.findIndex(item => item.id.toString() === active.id);
        const newIndex = items.findIndex(item => item.id.toString() === over?.id);
        return arrayMove(items, oldIndex, newIndex);
      });
    }
  };

  const handleAddItem = () => {
    const newItem: DictItemFormData = {
      id: `temp-${Date.now()}`,
      itemCode: '',
      itemName: '',
      status: 1,
      sort: items.length,
      isNew: true,
      isEditing: true,
    };
    setItems([...items, newItem]);
  };

  const handleEditItem = (id: number | string) => {
    setItems(items.map(item => 
      item.id === id ? { ...item, isEditing: true } : { ...item, isEditing: false }
    ));
  };

  const handleSaveItem = (id: number | string) => {
    setItems(items.map(item => 
      item.id === id ? { ...item, isEditing: false, isNew: false } : item
    ));
  };

  const handleCancelEdit = (id: number | string) => {
    setItems(items.map(item => {
      if (item.id === id) {
        if (item.isNew) {
          return null;
        }
        return { ...item, isEditing: false };
      }
      return item;
    }).filter(Boolean) as DictItemFormData[]);
  };

  const handleDeleteItem = (id: number | string) => {
    setItems(items.filter(item => item.id !== id));
  };

  const handleItemChange = (id: number | string, field: keyof DictItemFormData, value: any) => {
    setItems(items.map(item => 
      item.id === id ? { ...item, [field]: value } : item
    ));
  };

  const isReadOnly = mode === 'view';
  const canEdit = mode === 'create' || mode === 'edit';

  // 创建表格数据，包含添加按钮行
  const getTableDataSource = () => {
    const dataSource = [...items];
    
    // 如果可以编辑，添加一个特殊的添加按钮行
    if (canEdit) {
      dataSource.push({
        id: 'add-button-row',
        itemCode: '',
        itemName: '',
        status: 1,
        sort: items.length,
        isNew: false,
        isEditing: false,
        isAddButtonRow: true,
      } as DictItemFormData & { isAddButtonRow: boolean });
    }
    
    return dataSource;
  };

  const columns = [
    {
      key: 'sort',
      title: '排序',
      width: 60,
      onCell: (record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return {
            colSpan: canEdit ? 5 : 4, // 编辑模式5列（排序+编码+名称+状态+操作），查看模式4列（排序+编码+名称+状态）
          };
        }
        return {};
      },
      render: (_: any, record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return (
            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={handleAddItem}
              size="small"
              style={{ width: '100%' }}
            >
              添加字典项
            </Button>
          );
        }
        return null;
      },
    },
    {
      title: '字典项编码',
      dataIndex: 'itemCode',
      key: 'itemCode',
      onCell: (record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return {
            colSpan: 0, // 被排序列合并
          };
        }
        return {};
      },
      render: (text: string, record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return null;
        }
        
        if (record.isEditing && canEdit) {
          return (
            <Input
              value={text}
              onChange={(e) => handleItemChange(record.id, 'itemCode', e.target.value)}
              placeholder="请输入字典项编码"
              size="small"
            />
          );
        }
        return text;
      },
    },
    {
      title: '字典项名称',
      dataIndex: 'itemName',
      key: 'itemName',
      onCell: (record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return {
            colSpan: 0, // 被排序列合并
          };
        }
        return {};
      },
      render: (text: string, record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return null;
        }
        
        if (record.isEditing && canEdit) {
          return (
            <Input
              value={text}
              onChange={(e) => handleItemChange(record.id, 'itemName', e.target.value)}
              placeholder="请输入字典项名称"
              size="small"
            />
          );
        }
        return text;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      onCell: (record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return {
            colSpan: 0, // 被排序列合并
          };
        }
        return {};
      },
      render: (status: number, record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return null;
        }
        
        if (record.isEditing && canEdit) {
          return (
            <Switch
              size="small"
              checked={status === 1}
              onChange={(checked) => handleItemChange(record.id, 'status', checked ? 1 : 0)}
              checkedChildren="启用"
              unCheckedChildren="禁用"
            />
          );
        }
        return (
          <Switch
            size="small"
            checked={status === 1}
            disabled
            checkedChildren="启用"
            unCheckedChildren="禁用"
          />
        );
      },
    },
    ...(canEdit ? [{
      title: '操作',
      key: 'action',
      width: 120,
      onCell: (record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return {
            colSpan: 0, // 被排序列合并
          };
        }
        return {};
      },
      render: (_: any, record: DictItemFormData & { isAddButtonRow?: boolean }) => {
        if (record.isAddButtonRow) {
          return null;
        }
        
        if (record.isEditing) {
          return (
            <Space size="small">
              <Button
                type="link"
                size="small"
                icon={<SaveOutlined />}
                onClick={() => handleSaveItem(record.id)}
              >
                保存
              </Button>
              <Button
                type="link"
                size="small"
                icon={<CloseOutlined />}
                onClick={() => handleCancelEdit(record.id)}
              >
                取消
              </Button>
            </Space>
          );
        }
        return (
          <Space size="small">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEditItem(record.id)}
            >
              编辑
            </Button>
            <Popconfirm
              title="确定删除这个字典项吗？"
              onConfirm={() => handleDeleteItem(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              >
                删除
              </Button>
            </Popconfirm>
          </Space>
        );
      },
    }] : []),
  ];

  const getTitle = () => {
    switch (mode) {
      case 'create': return '新增字典';
      case 'edit': return '编辑字典';
      case 'view': return '字典详情';
      default: return '字典详情';
    }
  };

  const getFooter = () => {
    if (mode === 'view') {
      return [
        <Button key="close" onClick={onCancel}>
          关闭
        </Button>
      ];
    }
    
    return [
      <Button key="cancel" onClick={onCancel}>
        取消
      </Button>,
      <Button key="save" type="primary" loading={saving} onClick={handleSave}>
        保存
      </Button>
    ];
  };

  return (
    <Modal
      title={getTitle()}
      open={open}
      onCancel={onCancel}
      footer={getFooter()}
      width={900}
      destroyOnHidden
    >
      <div style={{ maxHeight: '70vh', overflowY: 'auto' }}>
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
              rules={[{ required: true, message: '请输入字典编码' }]}
            >
              <Input 
                placeholder="请输入字典编码"
                disabled={isReadOnly || mode === 'edit'} 
              />
            </Form.Item>
            <Form.Item
              name="dictName"
              label="字典名称"
              style={{ flex: 1 }}
              rules={[{ required: true, message: '请输入字典名称' }]}
            >
              <Input 
                placeholder="请输入字典名称"
                disabled={isReadOnly} 
              />
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
                disabled={isReadOnly}
                checkedChildren="启用" 
                unCheckedChildren="禁用" 
              />
            </Form.Item>
            {dict && (
              <div style={{ flex: 1 }}>
                <div style={{ marginBottom: 8, fontSize: 14, fontWeight: 500 }}>版本号</div>
                <div style={{ padding: '4px 11px', backgroundColor: '#f5f5f5', borderRadius: 6 }}>
                  {dict.version || '1'}
                </div>
              </div>
            )}
          </div>
          
          <Form.Item
            name="remark"
            label="备注"
          >
            <TextArea 
              rows={2} 
              disabled={isReadOnly}
              placeholder="请输入备注" 
            />
          </Form.Item>
        </Form>
        
        <div>
          <div style={{ marginBottom: 16 }}>
            <h4 style={{ margin: 0 }}>字典项列表</h4>
          </div>
          
          <DndContext
            sensors={sensors}
            collisionDetection={closestCenter}
            onDragEnd={onDragEnd}
          >
            <SortableContext
              items={items.map(item => item.id.toString())}
              strategy={verticalListSortingStrategy}
            >
              <Table
                components={{
                  body: {
                    row: SortableRow,
                  },
                }}
                columns={columns}
                dataSource={getTableDataSource()}
                rowKey="id"
                pagination={false}
                size="small"
                bordered
                onRow={(record) => {
                  // 添加按钮行不需要拖拽功能
                  if ((record as any).isAddButtonRow) {
                    return {};
                  }
                  return {
                    'data-row-key': record.id.toString(),
                  } as any;
                }}
              />
            </SortableContext>
          </DndContext>
          
          {items.length === 0 && (
            <div style={{ 
              textAlign: 'center', 
              padding: 40, 
              color: '#999',
              border: '1px dashed #d9d9d9',
              borderRadius: 6,
              marginTop: 16
            }}>
              {canEdit ? '暂无字典项，点击上方按钮添加' : '暂无字典项'}
            </div>
          )}
        </div>
      </div>
    </Modal>
  );
};

export default DictDetailModal;