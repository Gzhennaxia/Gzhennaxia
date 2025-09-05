import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message, Form, Input, DatePicker, Modal, Switch } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, EyeOutlined, CheckOutlined, StopOutlined } from '@ant-design/icons';
import { deleteDict, getDictPage } from '../../../services/dictService';
import DictDetailModal from './DictDetailModal';
import dayjs from 'dayjs';

import { Dict } from '../../../types/dict';

const DictList: React.FC = () => {
    const [dicts, setDicts] = useState<Dict[]>([]);
    const [loading, setLoading] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [currentDict, setCurrentDict] = useState<string>();
    const [modalMode, setModalMode] = useState<'create' | 'edit' | 'view'>('view');
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total: number) => `共 ${total} 条记录`,
    });

    const [searchForm] = Form.useForm();

    useEffect(() => {
        fetchDicts();
    }, []);

    const fetchDicts = async (params?: any) => {
        try {
            setLoading(true);
            const searchValues = searchForm.getFieldsValue();
            const query: Record<string, any> = {};

            if (searchValues.dictCode) query.dictCode = searchValues.dictCode;
            if (searchValues.dictName) query.dictName = searchValues.dictName;
            if (searchValues.status !== undefined) query.status = searchValues.status;
            if (searchValues.dateRange && searchValues.dateRange.length === 2) {
                query.startDate = searchValues.dateRange[0].format('YYYY-MM-DD');
                query.endDate = searchValues.dateRange[1].format('YYYY-MM-DD');
            }

            const requestParams = {
                pageNo: params?.current || pagination.current,
                pageSize: params?.pageSize || pagination.pageSize,
                query,
                sort: { created_time: 'desc' as const }
            };

            const response = await getDictPage(requestParams);
            setDicts(response.records || []);
            setPagination(prev => ({
                ...prev,
                current: response.current || 1,
                total: response.total || 0,
            }));
        } catch (error) {
            console.error('Failed to fetch dicts', error);
            message.error('获取字典列表失败');
        } finally {
            setLoading(false);
        }
    };

    const handleTableChange = (paginationConfig: any) => {
        fetchDicts(paginationConfig);
    };

    const handleSearch = () => {
        setPagination(prev => ({ ...prev, current: 1 }));
        fetchDicts({ current: 1, pageSize: pagination.pageSize });
    };

    const handleReset = () => {
        searchForm.resetFields();
        setPagination(prev => ({ ...prev, current: 1 }));
        fetchDicts({ current: 1, pageSize: pagination.pageSize });
    };

    const handleEdit = (record: Dict) => {
        setCurrentDict(record.dictCode);
        setModalMode('edit');
        setModalVisible(true);
    };

    const handleView = (record: Dict) => {
        setCurrentDict(record.dictCode);
        setModalMode('view');
        setModalVisible(true);
    };

    const handleCreate = () => {
        setCurrentDict(undefined);
        setModalMode('create');
        setModalVisible(true);
    };

    const handleDelete = async (dictCode: string) => {
        try {
            await deleteDict(dictCode);
            message.success('删除成功');
            fetchDicts();
        } catch (error) {
            console.error('Failed to delete dict', error);
            message.error('删除失败');
        }
    };

    const handleStatusChange = async (dictCode: string, status: number) => {
        try {
            // 这里应该调用更新状态的API
            message.success('状态更新成功');
            fetchDicts();
        } catch (error) {
            console.error('Failed to update status', error);
            message.error('状态更新失败');
        }
    };

    const handleBatchEnable = async () => {
        try {
            // 批量启用逻辑
            message.success('批量启用成功');
            setSelectedRowKeys([]);
            fetchDicts();
        } catch (error) {
            console.error('Failed to batch enable', error);
            message.error('批量启用失败');
        }
    };

    const handleBatchDisable = async () => {
        try {
            // 批量禁用逻辑
            message.success('批量禁用成功');
            setSelectedRowKeys([]);
            fetchDicts();
        } catch (error) {
            console.error('Failed to batch disable', error);
            message.error('批量禁用失败');
        }
    };

    const columns: ColumnsType<Dict> = [
        {
            title: '字典编码',
            dataIndex: 'dictCode',
            key: 'dictCode',
            width: 150,
        },
        {
            title: '字典名称',
            dataIndex: 'dictName',
            key: 'dictName',
            width: 200,
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 100,
            render: (status: number, record: Dict) => (
                <Switch
                    size="small"
                    checked={status === 1}
                    onChange={(checked) => handleStatusChange(record.dictCode, checked ? 1 : 0)}
                    checkedChildren="启用"
                    unCheckedChildren="禁用"
                />
            ),
        },
        {
            title: '版本号',
            dataIndex: 'version',
            key: 'version',
            width: 180,
        },
        {
            title: '备注',
            dataIndex: 'remark',
            key: 'remark',
            width: 180,
        },
        {
            title: '创建时间',
            dataIndex: 'createdTime',
            key: 'createdTime',
            width: 180,
            render: (time: string) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
        },
        {
            title: '更新时间',
            dataIndex: 'updatedTime',
            key: 'updatedTime',
            width: 180,
            render: (time: string) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
        },
        {
            title: '操作',
            key: 'action',
            width: 200,
            render: (_, record: Dict) => (
                <Space size="small">
                    <Button
                        type="link"
                        size="small"
                        icon={<EyeOutlined />}
                        onClick={() => handleView(record)}
                    >
                        查看
                    </Button>
                    <Button
                        type="link"
                        size="small"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    >
                        编辑
                    </Button>
                    <Button
                        type="link"
                        size="small"
                        icon={<CheckOutlined />}
                        onClick={() => handleStatusChange(record.dictCode, record.status === 1 ? 0 : 1)}
                    >
                        {record.status === 1 ? '禁用' : '启用'}
                    </Button>
                    <Button
                        type="link"
                        size="small"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => {
                            Modal.confirm({
                                title: '确认删除',
                                content: '确定要删除这个字典吗？',
                                onOk: () => handleDelete(record.dictCode),
                            });
                        }}
                    >
                        删除
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ padding: 24 }}>
            <Form
                form={searchForm}
                layout="inline"
                style={{ marginBottom: 16 }}
                onFinish={handleSearch}
            >
                <Form.Item name="dictCode" label="字典编码">
                    <Input placeholder="请输入字典编码" allowClear />
                </Form.Item>
                <Form.Item name="dictName" label="字典名称">
                    <Input placeholder="请输入字典名称" allowClear />
                </Form.Item>
                <Form.Item name="status" label="状态">
                    <div style={{ height: '32px', display: 'flex', alignItems: 'center' }}>
                        <Switch checkedChildren="启用" unCheckedChildren="禁用" />
                    </div>
                </Form.Item>
                <Form.Item name="dateRange" label="创建时间">
                    <DatePicker.RangePicker />
                </Form.Item>
                <Form.Item>
                    <Space>
                        <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                            搜索
                        </Button>
                        <Button onClick={handleReset}>
                            重置
                        </Button>
                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            onClick={handleCreate}
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
                rowKey="dictCode"
                loading={loading}
                pagination={pagination}
                onChange={handleTableChange}
                rowSelection={{
                    selectedRowKeys,
                    onChange: setSelectedRowKeys,
                }}
            />
            <DictDetailModal
              open={modalVisible}
              dictCode={currentDict}
              mode={modalMode}
              onCancel={() => setModalVisible(false)}
              onSuccess={() => {
                setModalVisible(false);
                fetchDicts();
              }}
            />
        </div>
    );
};

export default DictList;