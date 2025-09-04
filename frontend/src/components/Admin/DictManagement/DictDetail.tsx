import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Switch, Button, Space, message, Spin } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons';
import { getDictDetail, updateDict, createDict } from '../../../services/dictService';
import { DictType, DictItem } from '../../../types/dict';
import DraggableDictItemList from './DraggableDictItemList';
import dayjs from 'dayjs';

const { TextArea } = Input;

const DictDetail: React.FC = () => {
  const { code } = useParams<{ code: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [dict, setDict] = useState<DictType | null>(null);
  const [dictItems, setDictItems] = useState<DictItem[]>([]);
  const isNew = code === 'new';

  useEffect(() => {
    if (!isNew && code) {
      fetchDictDetail(code);
    } else {
      // 新建模式，初始化空数据
      const newDict: DictType = {
        id: 0,
        code: '',
        name: '',
        version: '1.0',
        status: 1,
        remark: '',
        items: [],
        created_time: '',
        updated_time: '',
      };
      setDict(newDict);
      setDictItems([]);
      form.setFieldsValue(newDict);
    }
  }, [code, isNew, form]);

  const fetchDictDetail = async (dictCode: string) => {
    setLoading(true);
    try {
      const data = await getDictDetail(dictCode);
      setDict(data);
      setDictItems(data.items || []);
      form.setFieldsValue({
        code: data.code,
        name: data.name,
        status: data.status === 1,
        remark: data.remark,
      });
    } catch (error) {
      message.error('获取字典详情失败');
      navigate('/admin/dict');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      
      // 验证字典项
      if (dictItems.length === 0) {
        message.error('请至少添加一个字典项');
        return;
      }

      // 验证字典项数据
      const errors: string[] = [];
      const valueSet = new Set<string>();
      
      dictItems.forEach((item, index) => {
        if (!item.item_key.trim()) {
          errors.push(`第${index + 1}行：标签不能为空`);
        }
        if (!item.item_value.trim()) {
          errors.push(`第${index + 1}行：值不能为空`);
        }
        if (valueSet.has(item.item_value)) {
          errors.push(`第${index + 1}行：值"${item.item_value}"重复`);
        } else {
          valueSet.add(item.item_value);
        }
      });

      if (errors.length > 0) {
        message.error(errors.join('\n'));
        return;
      }

      setSaving(true);

      const dictData = {
        code: values.code,
        name: values.name,
        status: values.status ? 1 : 0,
        remark: values.remark,
        items: dictItems.map((item, index) => ({
          ...item,
          sort: index + 1,
          type_code: values.code,
        })),
      };

      if (isNew) {
        await createDict(dictData);
        message.success('字典创建成功');
      } else {
        await updateDict(code!, dictData);
        message.success('字典更新成功');
      }

      navigate('/admin/dict');
    } catch (error) {
      console.error('Save failed:', error);
      message.error(isNew ? '创建失败' : '更新失败');
    } finally {
      setSaving(false);
    }
  };

  const handleItemsChange = (items: DictItem[]) => {
    setDictItems(items);
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <Card
        title={
          <Space>
            <Button
              type="text"
              icon={<ArrowLeftOutlined />}
              onClick={() => navigate('/admin/dict')}
            />
            <span>{isNew ? '新建字典' : `编辑字典 - ${dict?.name}`}</span>
            {!isNew && dict && (
              <span style={{ fontSize: '12px', color: '#666' }}>
                版本: {dict.version}
              </span>
            )}
          </Space>
        }
        extra={
          <Button
            type="primary"
            icon={<SaveOutlined />}
            loading={saving}
            onClick={handleSave}
          >
            保存
          </Button>
        }
        style={{ marginBottom: '16px' }}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            status: true,
          }}
        >
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <Form.Item
              label="字典编码"
              name="code"
              rules={[
                { required: true, message: '请输入字典编码' },
                { pattern: /^[A-Z0-9_]+$/, message: '只能包含大写字母、数字和下划线' },
              ]}
            >
              <Input 
                placeholder="请输入字典编码" 
                disabled={!isNew}
                style={{ textTransform: 'uppercase' }}
              />
            </Form.Item>

            <Form.Item
              label="字典名称"
              name="name"
              rules={[{ required: true, message: '请输入字典名称' }]}
            >
              <Input placeholder="请输入字典名称" />
            </Form.Item>
          </div>

          <Form.Item
            label="状态"
            name="status"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remark"
          >
            <TextArea 
              placeholder="请输入备注信息" 
              rows={3}
              maxLength={500}
              showCount
            />
          </Form.Item>

          {!isNew && dict && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div>
                <label style={{ fontSize: '14px', color: '#666' }}>创建时间</label>
                <div style={{ padding: '4px 0' }}>
                  {dict.created_time ? dayjs(dict.created_time).format('YYYY-MM-DD HH:mm:ss') : '-'}
                </div>
              </div>
              <div>
                <label style={{ fontSize: '14px', color: '#666' }}>更新时间</label>
                <div style={{ padding: '4px 0' }}>
                  {dict.updated_time ? dayjs(dict.updated_time).format('YYYY-MM-DD HH:mm:ss') : '-'}
                </div>
              </div>
            </div>
          )}
        </Form>
      </Card>

      <DraggableDictItemList
        items={dictItems}
        onChange={handleItemsChange}
      />
    </div>
  );
};

export default DictDetail;