import React, { useState } from 'react';
import { Modal, Upload, Alert, Table, Button, Space, Typography, message } from 'antd';
import { InboxOutlined, DownloadOutlined } from '@ant-design/icons';
import type { UploadFile } from 'antd/es/upload/interface';
import { accountingService } from '../../services/accountingService';
import { downloadTransactionImportTemplate } from '../../constants/transactionImportTemplate';
import type { AccTransactionImportResult } from '../../types/Accounting';

const { Dragger } = Upload;
const { Text, Paragraph } = Typography;

interface TransactionImportModalProps {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

/**
 * 流水批量导入弹窗（CSV / Excel）。
 */
const TransactionImportModal: React.FC<TransactionImportModalProps> = ({ open, onClose, onSuccess }) => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [importing, setImporting] = useState(false);
  const [result, setResult] = useState<AccTransactionImportResult | null>(null);

  const reset = () => {
    setFileList([]);
    setResult(null);
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  const handleImport = async () => {
    const file = fileList[0]?.originFileObj;
    if (!file) {
      message.warning('请先选择文件');
      return;
    }
    setImporting(true);
    try {
      const res = await accountingService.importTransactions(file);
      setResult(res);
      if (res.successCount > 0) {
        onSuccess();
      }
      if (res.failCount === 0) {
        message.success(`成功导入 ${res.successCount} 条`);
      } else if (res.successCount > 0) {
        message.warning(`成功 ${res.successCount} 条，失败 ${res.failCount} 条`);
      } else {
        message.error('导入失败，请检查文件与失败明细');
      }
    } catch (e: unknown) {
      const err = e as { message?: string };
      message.error(err.message || '导入失败');
    } finally {
      setImporting(false);
    }
  };

  return (
    <Modal
      title="批量导入流水"
      open={open}
      onCancel={handleClose}
      width={720}
      footer={
        <Space>
          <Button onClick={handleClose}>关闭</Button>
          <Button type="primary" loading={importing} onClick={handleImport} disabled={fileList.length === 0}>
            开始导入
          </Button>
        </Space>
      }
      destroyOnClose
    >
      <Paragraph type="secondary" style={{ marginBottom: 12 }}>
        支持 CSV、XLS、XLSX。表头须包含：<Text strong>类型、金额、账户</Text>；可选：分类、交易时间、商户、备注、目标账户（转账必填）、标签。
        类型填：支出 / 收入 / 转账；账户、分类名称须与系统中已有名称一致；标签多个用英文逗号分隔，不存在则自动创建。
      </Paragraph>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<DownloadOutlined />} onClick={downloadTransactionImportTemplate}>
          下载 CSV 模板
        </Button>
      </Space>
      <Dragger
        accept=".csv,.xlsx,.xls"
        maxCount={1}
        fileList={fileList}
        beforeUpload={() => false}
        onChange={({ fileList: list }) => {
          setFileList(list.slice(-1));
          setResult(null);
        }}
        onRemove={() => {
          setFileList([]);
          setResult(null);
        }}
      >
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">点击或拖拽文件到此处</p>
        <p className="ant-upload-hint">支持 .csv、.xlsx、.xls，单次最多 5000 行</p>
      </Dragger>
      {result && (
        <div style={{ marginTop: 16 }}>
          <Alert
            type={result.failCount === 0 ? 'success' : result.successCount > 0 ? 'warning' : 'error'}
            showIcon
            message={`共 ${result.totalRows} 行：成功 ${result.successCount}，失败 ${result.failCount}`}
            style={{ marginBottom: 12 }}
          />
          {result.errors.length > 0 && (
            <Table
              size="small"
              rowKey={(r) => `${r.rowNumber}-${r.message}`}
              pagination={{ pageSize: 5, hideOnSinglePage: true }}
              dataSource={result.errors}
              columns={[
                { title: '行号', dataIndex: 'rowNumber', width: 72 },
                { title: '原因', dataIndex: 'message' },
              ]}
            />
          )}
        </div>
      )}
    </Modal>
  );
};

export default TransactionImportModal;
