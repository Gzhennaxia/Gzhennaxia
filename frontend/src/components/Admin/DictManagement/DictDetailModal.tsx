import React, { useState, useEffect } from 'react';
import { Modal, Descriptions, Tag, Button } from 'antd';
import { getDictDetail } from '../../../services/dictService';
import { Dict } from '../../../types/dict';
import dayjs from 'dayjs';

interface DictDetailModalProps {
    open: boolean;
    dictCode: string;
    onCancel: () => void;
}

const DictDetailModal: React.FC<DictDetailModalProps> = ({ open, dictCode, onCancel }) => {
    const [dict, setDict] = useState<Dict | null>(null);
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
                    <Descriptions.Item label="创建时间">
                        {dict.createdTime ? dayjs(dict.createdTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="更新时间">
                        {dict.updatedTime ? dayjs(dict.updatedTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
                    </Descriptions.Item>
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