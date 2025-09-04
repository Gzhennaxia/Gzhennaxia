import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Switch, Button, Space, message, Spin } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons';
import { getDictDetail, updateDict, createDict } from '../../../services/dictService';
import { Dict, DictItem } from '../../../types/dict';
import DraggableDictItemList from './DraggableDictItemList';
import dayjs from 'dayjs';

const { TextArea } = Input;

const DictDetail: React.FC = () => {
  const { dictCode } = useParams<{ dictCode: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [dict, setDict] = useState<Dict | null>(null);
  const [dictItems, setDictItems] = useState<DictItem[]>([]);
  const isNew = dictCode === 'new';

  useEffect(() => {
    if (!isNew && dictCode) {
      fetchDictDetail(dictCode);
    } else {
      // 新建模式，初始化空数据
      const newDict: Dict = {
        id: 0,
        dictCode: '',
        dictName: '',
        version: '1.0',
        status: 1,
        remark: '',
        items: [],
        createdTime: '',
        updatedTime: '',
      };
      setDict(newDict);
      setDictItems([]);
      form.setFieldsValue(newDict);
    }
  }, [dictCode, isNew, form]);

  const fetchDictDetail = async (dictCode: string) => {
    setLoading(true);
    try {
      const data = await getDictDetail(dictCode);
      setDict(data);
      setDictItems(data.items || []);
      form.setFieldsValue({
        dictCode: data.dictCode,
        dictName: data.dictName,
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
        if (!item.itemCode.trim()) {
          errors.push(`第${index + 1}行：标签不能为空`);
        }
        if (!item.itemName.trim()) {
          errors.push(`第${index + 1}行：值不能为空`);
        }
        if (valueSet.has(item.itemName)) {
          errors.push(`第${index + 1}行：值"${item.itemName}"重复`);
        } else {
          valueSet.add(item.itemName);
        }
      });

      if (errors.length > 0) {
        message.error(errors.join('\n'));
        return;
      }

      setSaving(true);

      const dictData = {
        dictCode: values.dictCode,
        dictName: values.dictName,
        status: values.status ? 1 : 0,
        remark: values.remark,
        items: dictItems.map((item, index) => ({
          ...item,
          sort: index + 1,
          dictCode: values.dictCode,
        })),
      };

      if (isNew) {
        await createDict(dictData);
        message.success('字典创建成功');
      } else {
        await updateDict(dictCode!, dictData);
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
            <span>{isNew ? '新建字典' : `编辑字典 - ${dict?.dictName}`}</span>
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
              name="dictCode"
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
              name="dictName"
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
                  {dict.createdTime ? dayjs(dict.createdTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
                </div>
              </div>
              <div>
                <label style={{ fontSize: '14px', color: '#666' }}>更新时间</label>
                <div style={{ padding: '4px 0' }}>
                  {dict.updatedTime ? dayjs(dict.updatedTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
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