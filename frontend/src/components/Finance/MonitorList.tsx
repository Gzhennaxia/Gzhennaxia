import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Card, Tag, Modal, Form, Input, InputNumber, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

interface StockMonitorData {
  id?: number;
  exchange: string;
  stockCode: string;
  stockName: string;
  currencySymbol: string;
  price1yAgo?: number;
  rise1y?: number;
  price6mAgo?: number;
  rise6m?: number;
  price3mAgo?: number;
  rise3m?: number;
  price1mAgo?: number;
  rise1m?: number;
  price1wAgo?: number;
  rise1w?: number;
  price3dAgo?: number;
  rise3d?: number;
  priceYesterday?: number;
  riseYesterday?: number;
  cacheExpireTime?: string;
}

const MonitorList: React.FC = () => {
  const [data, setData] = useState<StockMonitorData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [cacheInfo, setCacheInfo] = useState<string>('');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingRecord, setEditingRecord] = useState<StockMonitorData | null>(null);

  useEffect(() => {
    fetchData();
    // 模拟定时刷新数据
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      // 模拟API调用
      // 实际应该使用: const response = await axios.get('/api/finance/stock-monitor/page');
      // 暂时使用模拟数据
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockData: StockMonitorData[] = [
        {
          id: 1,
          exchange: "纳斯达克",
          stockCode: "AAPL",
          stockName: "苹果",
          currencySymbol: "$",
          price1yAgo: 180.25,
          rise1y: 15.80,
          price6mAgo: 195.70,
          rise6m: 7.25,
          price3mAgo: 210.10,
          rise3m: 2.40,
          price1mAgo: 218.90,
          rise1m: -1.10,
          price1wAgo: 215.50,
          rise1w: 1.85,
          price3dAgo: 218.30,
          rise3d: 0.55,
          priceYesterday: 220.10,
          riseYesterday: 0.95,
        },
        {
          id: 2,
          exchange: "上交所&深交所",
          stockCode: "指数",
          stockName: "沪深300",
          currencySymbol: "",
          price1yAgo: 4120.50,
          rise1y: 8.65,
          price6mAgo: 4350.20,
          rise6m: 3.20,
          price3mAgo: 4480.80,
          rise3m: -1.50,
          price1mAgo: 4420.30,
          rise1m: 0.85,
          price1wAgo: 4450.70,
          rise1w: -0.30,
          price3dAgo: 4445.20,
          rise3d: 0.45,
          priceYesterday: 4460.10,
          riseYesterday: 0.25,
        },
        {
          id: 3,
          exchange: "香港",
          stockCode: "指数",
          stockName: "恒生指数",
          currencySymbol: "",
          price1yAgo: 19850.30,
          rise1y: 12.30,
          price6mAgo: 21500.80,
          rise6m: 5.85,
          price3mAgo: 22800.50,
          rise3m: -0.90,
          price1mAgo: 22650.20,
          rise1m: 1.20,
          price1wAgo: 22850.70,
          rise1w: 0.55,
          price3dAgo: 22900.30,
          rise3d: 0.30,
          priceYesterday: 22980.50,
          riseYesterday: 0.15,
        },
        {
          id: 4,
          exchange: "纳斯达克&纽交所",
          stockCode: "指数",
          stockName: "标准普尔500",
          currencySymbol: "",
          price1yAgo: 4250.70,
          rise1y: 18.50,
          price6mAgo: 4580.30,
          rise6m: 9.20,
          price3mAgo: 4820.50,
          rise3m: 3.80,
          price1mAgo: 4980.20,
          rise1m: 1.50,
          price1wAgo: 5050.80,
          rise1w: 0.75,
          price3dAgo: 5080.40,
          rise3d: 0.40,
          priceYesterday: 5100.90,
          riseYesterday: 0.20,
        }
      ];
      
      setData(mockData);
      setCacheInfo(`缓存状态：有效 | 过期时间：${new Date(Date.now() + 30 * 60 * 1000).toLocaleString()} | 剩余时间：29分59秒`);
    } catch (error) {
      console.error('获取数据失败:', error);
      message.error('获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  const formatNumber = (num?: number): string => {
    if (num === undefined || num === null) return '-';
    return num.toFixed(2);
  };

  const renderRiseStyle = (rise?: number): React.ReactNode => {
    if (rise === undefined || rise === null) return '-';
    
    let color = '';
    if (rise > 0) {
      color = '#ef4444'; // red for positive
    } else if (rise < 0) {
      color = '#10b981'; // green for negative
    }
    
    const sign = rise > 0 ? '+' : '';
    return <span style={{ color }}>{sign}{formatNumber(rise)}%</span>;
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (record: StockMonitorData) => {
    setEditingRecord(record);
    form.setFieldsValue(record);
    setIsModalVisible(true);
  };

  const handleDelete = (id?: number) => {
    if (!id) return;
    // 模拟删除操作
    message.success('删除成功');
    fetchData(); // 重新加载数据
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      // 模拟保存操作
      if (editingRecord) {
        message.success('更新成功');
      } else {
        message.success('添加成功');
      }
      setIsModalVisible(false);
      fetchData(); // 重新加载数据
    } catch (error) {
      console.error('保存失败:', error);
      message.error('保存失败');
    }
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
  };

  const columns: ColumnsType<StockMonitorData> = [
    {
      title: '交易所',
      dataIndex: 'exchange',
      key: 'exchange',
      width: 120,
    },
    {
      title: '股票编号',
      dataIndex: 'stockCode',
      key: 'stockCode',
      width: 100,
    },
    {
      title: '中文名',
      dataIndex: 'stockName',
      key: 'stockName',
      width: 120,
    },
    {
      title: '一年前收盘价',
      dataIndex: 'price1yAgo',
      key: 'price1yAgo',
      width: 120,
      render: (_, record) => record.currencySymbol + formatNumber(record.price1yAgo),
    },
    {
      title: '一年内涨幅',
      dataIndex: 'rise1y',
      key: 'rise1y',
      width: 120,
      render: (_, record) => renderRiseStyle(record.rise1y),
    },
    {
      title: '半年前收盘价',
      dataIndex: 'price6mAgo',
      key: 'price6mAgo',
      width: 130,
      render: (_, record) => record.currencySymbol + formatNumber(record.price6mAgo),
    },
    {
      title: '半年内涨幅',
      dataIndex: 'rise6m',
      key: 'rise6m',
      width: 120,
      render: (_, record) => renderRiseStyle(record.rise6m),
    },
    {
      title: '3个月前收盘价',
      dataIndex: 'price3mAgo',
      key: 'price3mAgo',
      width: 140,
      render: (_, record) => record.currencySymbol + formatNumber(record.price3mAgo),
    },
    {
      title: '3个月内涨幅',
      dataIndex: 'rise3m',
      key: 'rise3m',
      width: 130,
      render: (_, record) => renderRiseStyle(record.rise3m),
    },
    {
      title: '1月前收盘价',
      dataIndex: 'price1mAgo',
      key: 'price1mAgo',
      width: 120,
      render: (_, record) => record.currencySymbol + formatNumber(record.price1mAgo),
    },
    {
      title: '1月内涨幅',
      dataIndex: 'rise1m',
      key: 'rise1m',
      width: 120,
      render: (_, record) => renderRiseStyle(record.rise1m),
    },
    {
      title: '1周前收盘价',
      dataIndex: 'price1wAgo',
      key: 'price1wAgo',
      width: 120,
      render: (_, record) => record.currencySymbol + formatNumber(record.price1wAgo),
    },
    {
      title: '1周内涨幅',
      dataIndex: 'rise1w',
      key: 'rise1w',
      width: 120,
      render: (_, record) => renderRiseStyle(record.rise1w),
    },
    {
      title: '3日前收盘价',
      dataIndex: 'price3dAgo',
      key: 'price3dAgo',
      width: 120,
      render: (_, record) => record.currencySymbol + formatNumber(record.price3dAgo),
    },
    {
      title: '3日内涨幅',
      dataIndex: 'rise3d',
      key: 'rise3d',
      width: 120,
      render: (_, record) => renderRiseStyle(record.rise3d),
    },
    {
      title: '昨日收盘价',
      dataIndex: 'priceYesterday',
      key: 'priceYesterday',
      width: 120,
      render: (_, record) => record.currencySymbol + formatNumber(record.priceYesterday),
    },
    {
      title: '昨日涨幅',
      dataIndex: 'riseYesterday',
      key: 'riseYesterday',
      width: 120,
      render: (_, record) => renderRiseStyle(record.riseYesterday),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
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

  return (
    <Card
      title="股票/指数涨幅监控列表"
      extra={
        <Space>
          <Tag color="blue">{cacheInfo}</Tag>
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={handleAdd}
          >
            新增监控项
          </Button>
        </Space>
      }
      style={{ margin: 20 }}
    >
      <Table
        columns={columns}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        size="small"
        loading={loading}
        rowKey="id"
      />

      <Modal
        title={editingRecord ? "编辑监控项" : "新增监控项"}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Form.Item 
            name="exchange" 
            label="交易所" 
            rules={[{ required: true, message: '请输入交易所!' }]}
          >
            <Input />
          </Form.Item>
          
          <Form.Item 
            name="stockCode" 
            label="股票编号" 
            rules={[{ required: true, message: '请输入股票编号!' }]}
          >
            <Input />
          </Form.Item>
          
          <Form.Item 
            name="stockName" 
            label="中文名" 
            rules={[{ required: true, message: '请输入中文名!' }]}
          >
            <Input />
          </Form.Item>
          
          <Form.Item 
            name="currencySymbol" 
            label="币种符号"
          >
            <Input />
          </Form.Item>
          
          <Form.Item 
            name="price1yAgo" 
            label="一年前收盘价"
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          
          <Form.Item 
            name="rise1y" 
            label="一年内涨幅 (%)"
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default MonitorList;