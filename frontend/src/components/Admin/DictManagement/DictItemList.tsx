import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

interface DictItem {
    id: number;
    item_key: string;
    item_value: string;
    sort: number;
    status: number;
    remark?: string;
}

const DictItemList: React.FC = () => {
    const { code } = useParams<{ code: string }>();
    const navigate = useNavigate();
    const [items, setItems] = useState<DictItem[]>([]);
    const [loading, setLoading] = useState(false);
    const [dictInfo, setDictInfo] = useState({ name: '', version: '' });

    const columns: ColumnsType<DictItem> = [
        {
            title: '键名',
            dataIndex: 'item_key',
            key: 'item_key',
        },
        {
            title: '键值',
            dataIndex: 'item_value',
            key: 'item_value',
        },
        {
            title: '排序',
            dataIndex: 'sort',
            key: 'sort',
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => (
                <Tag color={status === 1 ? 'success' : 'error'}>
                    {status === 1 ? '启用' : '禁用'}
                </Tag>
            ),
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
                        onClick={() => handleDelete(record.id)}
                    />
                </Space>
            ),
        },
    ];

    useEffect(() => {
        if (code) {
            fetchDictItems(code);
            fetchDictInfo(code);
        }
    }, [code]);

    const fetchDictItems = async (dictCode: string) => {
        setLoading(true);
        try {
            const data = await getDictItems(dictCode);
            setItems(data);
        } catch (error) {
            message.error('获取字典项列表失败');
        } finally {
            setLoading(false);
        }
    };

    const fetchDictInfo = async (dictCode: string) => {
        try {
            const dict = DictCacheManager.getDict(dictCode);
            if (dict) {
                setDictInfo({
                    name: dict.name,
                    version: dict.version
                });
            } else {
                const freshDict = await getDict(dictCode);
                setDictInfo({
                    name: freshDict.name,
                    version: freshDict.version
                });
            }
        } catch (error) {
            message.error('获取字典信息失败');
        }
    };

    const handleEdit = (record: DictItem) => {
        // TODO: 实现编辑逻辑
        console.log('Edit:', record);
    };

    const handleDelete = (id: number) => {
        // TODO: 实现删除逻辑
        console.log('Delete:', id);
    };

    return (
        <Card
            title={
                <Space>
                    <Button
                        type="text"
                        icon={<ArrowLeftOutlined />}
                        onClick={() => navigate('/admin/dict')}
                    />
                    <span>{dictInfo.name} ({code})</span>
                    <Tag>版本: {dictInfo.version}</Tag>
                </Space>
            }
            extra={
                <Button type="primary" icon={<PlusOutlined />}>
                    新增字典项
                </Button>
            }
        >
            <Table
                columns={columns}
                dataSource={items}
                rowKey="id"
                loading={loading}
            />
        </Card>
    );
};

export default DictItemList;