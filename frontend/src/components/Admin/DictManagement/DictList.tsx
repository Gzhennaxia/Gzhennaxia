import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import DictCacheManager from '../../../services/dictCacheManager';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getDictList, deleteDict } from '../../../services/dictService';

interface DictType {
    id: number;
    code: string;
    name: string;
    version: string;
    status: number;
    remark?: string;
}

const DictList: React.FC = () => {
    const [dicts, setDicts] = useState<DictType[]>([]);
    const [loading, setLoading] = useState(false);

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
    }, []);

    const fetchDicts = async () => {
        setLoading(true);
        try {
            const data = await getDictList();
            setDicts(data);
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

    const handleDelete = (code: string) => {
        // TODO: 实现删除逻辑
        console.log('Delete:', code);
    };

    return (
        <div>
            <div style={{ marginBottom: 16 }}>
                <Button type="primary" icon={<PlusOutlined />}>
                    新增字典
                </Button>
            </div>
            <Table 
                columns={columns} 
                dataSource={dicts} 
                rowKey="code"
                loading={loading}
            />
        </div>
    );
};

export default DictList;