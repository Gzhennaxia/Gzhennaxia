import React, { useState, useEffect } from 'react';
import { Modal, Descriptions, Tag, Button } from 'antd';
import { getDictDetail } from '../../../services/dictService';

interface DictType {
    id: number;
    code: string;
    name: string;
    version: string;
    status: number;
    remark?: string;
    items?: Array<{
        id: number;
        item_key: string;
        item_value: string;
        status: number;
        sort: number;
    }>;
}

interface DictDetailModalProps {
    open: boolean;
    dictCode: string;
    onCancel: () => void;
}

const DictDetailModal: React.FC<DictDetailModalProps> = ({ open, dictCode, onCancel }) => {
    const [dict, setDict] = useState<DictType | null>(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (open && dictCode) {
            fetchDictDetail(dictCode);
        }
    }, [open, dictCode]);

    const fetchDictDetail = async (code: string) => {
        try {
            setLoading(true);
            const data = await getDictDetail(code);
            setDict(data);
        } catch (error) {
            console.error('Failed to fetch dict detail', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title="字典详情"
            open={open}
            onCancel={onCancel}
            footer={[
                <Button key="back" onClick={onCancel}>
                    关闭
                </Button>
            ]}
            width={800}
        >
            {dict && (
                <Descriptions bordered column={1}>
                    <Descriptions.Item label="字典编码">{dict.code}</Descriptions.Item>
                    <Descriptions.Item label="字典名称">{dict.name}</Descriptions.Item>
                    <Descriptions.Item label="版本号">{dict.version}</Descriptions.Item>
                    <Descriptions.Item label="状态">
                        <Tag color={dict.status === 1 ? 'green' : 'red'}>
                            {dict.status === 1 ? '启用' : '禁用'}
                        </Tag>
                    </Descriptions.Item>
                    <Descriptions.Item label="备注">{dict.remark || '-'}</Descriptions.Item>
                    <Descriptions.Item label="字典项">
                        {dict.items?.map(item => (
                            <div key={item.id} style={{ marginBottom: 8 }}>
                                <Tag>{item.item_key}</Tag>
                                <span style={{ margin: '0 8px' }}>{item.item_value}</span>
                                <Tag color={item.status === 1 ? 'green' : 'red'}>
                                    {item.status === 1 ? '启用' : '禁用'}
                                </Tag>
                            </div>
                        ))}
                    </Descriptions.Item>
                </Descriptions>
            )}
        </Modal>
    );
};

export default DictDetailModal;