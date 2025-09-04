import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message, Form, Input, DatePicker, Modal } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import DictCacheManager from '../../../services/dictCacheManager';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, EyeOutlined } from '@ant-design/icons';
import { getDictList, deleteDict, getDictPage } from '../../../services/dictService';
import DictFormModal from './DictFormModal';
import DictDetailModal from './DictDetailModal';
import dayjs from 'dayjs';

import { DictType, PageParams } from '../../../types/dict';

const DictList: React.FC = () => {
    const [dicts, setDicts] = useState<DictType[]>([]);
    const [loading, setLoading] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [detailVisible, setDetailVisible] = useState(false);
    const [currentDict, setCurrentDict] = useState<string>();
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
    });
    const [searchForm] = Form.useForm();

    const columns: ColumnsType<DictType> = [
        {
            title: '字典编码',
            dataIndex: 'code',
            key: 'code',
        },
        {
            title: '字典名称',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '版本号',
            dataIndex: 'version',
            key: 'version',
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => (status === 1 ? '启用' : '禁用'),
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button
                        type="text"
                        icon={<EyeOutlined />}
                        onClick={() => {
                            setCurrentDict(record.code);
                            setDetailVisible(true);
                        }}
                    />
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    />
                    <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(record.code)}
                    />
                </Space>
            ),
        },
    ];

    useEffect(() => {
        fetchDicts();
    }, [pagination.current, pagination.pageSize]);

    const fetchDicts = async () => {
        setLoading(true);
        try {
            const values = searchForm.getFieldsValue();
            const query: Record<string, any> = {};

            if (values.code) query.code_like = values.code;
            if (values.createdTime) {
                query.createdTime_ge = dayjs(values.createdTime[0]).format('YYYY-MM-DD HH:mm:ss');
                query.createdTime_le = dayjs(values.createdTime[1]).format('YYYY-MM-DD HH:mm:ss');
            }

            const params = {
                pageNo: pagination.current,
                pageSize: pagination.pageSize,
                query,
                sort: { createdTime: 'desc' }
            };

            const { records, total } = await getDictPage(params);
            setDicts(records);
            setPagination({ ...pagination, total });
        } catch (error) {
            message.error('获取字典列表失败');
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (record: DictType) => {
        // TODO: 跳转到编辑页面
        console.log('Edit:', record);
    };

  const handleDelete = async (code: string) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除字典 ${code} 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDict(code);
          message.success('删除成功');
          fetchDicts();
        } catch (error) {
          message.error('删除失败');
        }
      }
    });
  };

    const handleTableChange = (pagination: any) => {
        setPagination(pagination);
    };

    const handleSearch = () => {
        setPagination({ ...pagination, current: 1 });
        fetchDicts();
    };

    const handleReset = () => {
        searchForm.resetFields();
        setPagination({ ...pagination, current: 1 });
        fetchDicts();
    };

    return (
        <div>
            <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
                <Form.Item name="code" label="字典编码">
                    <Input placeholder="请输入字典编码" />
                </Form.Item>
                <Form.Item name="createdTime" label="创建时间">
                    <DatePicker.RangePicker showTime />
                </Form.Item>
                <Form.Item>
                    <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                        搜索
                    </Button>
                </Form.Item>
                <Form.Item>
                    <Button onClick={handleReset}>重置</Button>
                </Form.Item>
                <Form.Item>
                    <Button
                      type="primary"
                      icon={<PlusOutlined />}
                      onClick={() => setModalVisible(true)}
                    >
                        新增字典
                    </Button>
                </Form.Item>
            </Form>
            <Table
                columns={columns}
                dataSource={dicts}
                rowKey="code"
                loading={loading}
                pagination={pagination}
                onChange={handleTableChange}
            />
            <DictFormModal
              open={modalVisible}
              onCancel={() => setModalVisible(false)}
              onSuccess={() => {
                setModalVisible(false);
                fetchDicts();
              }}
            />
            <DictDetailModal
              open={detailVisible}
              dictCode={currentDict}
              onCancel={() => setDetailVisible(false)}
            />
        </div>
    );
};

export default DictList;