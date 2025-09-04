import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message, Form, Input, DatePicker, Modal, Switch } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import DictCacheManager from '../../../services/dictCacheManager';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, EyeOutlined, CheckOutlined, StopOutlined } from '@ant-design/icons';
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
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
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
            render: (status: number, record: DictType) => (
                <Switch
                    checked={status === 1}
                    onChange={(checked) => handleStatusChange(record.code, checked)}
                    checkedChildren="启用"
                    unCheckedChildren="禁用"
                />
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createdTime',
            key: 'createdTime',
            render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
        },
        {
            title: '更新时间',
            dataIndex: 'updatedTime',
            key: 'updatedTime',
            render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
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
                        icon={record.status === 1 ? <StopOutlined /> : <CheckOutlined />}
                        onClick={() => handleStatusChange(record.code, record.status !== 1)}
                    >
                        {record.status === 1 ? '禁用' : '启用'}
                    </Button>
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
                sort: { created_time: 'desc' as 'desc' }
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
        setCurrentDict(record.code);
        setDetailVisible(true);
    };

    const handleStatusChange = async (code: string, enabled: boolean) => {
        try {
            // 调用API更新状态
            const response = await fetch(`/api/dict/${code}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status: enabled ? 1 : 0 }),
            });
            
            if (response.ok) {
                message.success(enabled ? '启用成功' : '禁用成功');
                fetchDicts();
            } else {
                message.error('状态更新失败');
            }
        } catch (error) {
            message.error('状态更新失败');
        }
    };

    const handleBatchEnable = async () => {
        if (selectedRowKeys.length === 0) {
            message.warning('请选择要启用的字典项');
            return;
        }
        try {
            await Promise.all(
                selectedRowKeys.map(code => 
                    fetch(`/api/dict/${code}/status`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ status: 1 }),
                    })
                )
            );
            message.success('批量启用成功');
            fetchDicts();
            setSelectedRowKeys([]);
        } catch (error) {
            message.error('批量启用失败');
        }
    };

    const handleBatchDisable = async () => {
        if (selectedRowKeys.length === 0) {
            message.warning('请选择要禁用的字典项');
            return;
        }
        try {
            await Promise.all(
                selectedRowKeys.map(code => 
                    fetch(`/api/dict/${code}/status`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ status: 0 }),
                    })
                )
            );
            message.success('批量禁用成功');
            fetchDicts();
            setSelectedRowKeys([]);
        } catch (error) {
            message.error('批量禁用失败');
        }
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
                    <Space>
                        <Button
                          type="primary"
                          icon={<PlusOutlined />}
                          onClick={() => setModalVisible(true)}
                        >
                            新增字典
                        </Button>
                        <Button 
                            icon={<CheckOutlined />} 
                            onClick={handleBatchEnable}
                            disabled={selectedRowKeys.length === 0}
                        >
                            批量启用
                        </Button>
                        <Button 
                            icon={<StopOutlined />} 
                            onClick={handleBatchDisable}
                            disabled={selectedRowKeys.length === 0}
                        >
                            批量禁用
                        </Button>
                    </Space>
                </Form.Item>
            </Form>
            <Table
                columns={columns}
                dataSource={dicts}
                rowKey="code"
                loading={loading}
                pagination={pagination}
                onChange={handleTableChange}
                rowSelection={{
                    selectedRowKeys,
                    onChange: setSelectedRowKeys,
                }}
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
              dictCode={currentDict || ''}
              onCancel={() => setDetailVisible(false)}
            />
        </div>
    );
};

export default DictList;