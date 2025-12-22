import React, { useState, useEffect } from 'react';

interface StockMonitorData {
  id: number;
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
    } finally {
      setLoading(false);
    }
  };

  const formatNumber = (num?: number): string => {
    if (num === undefined) return '-';
    return num.toFixed(2);
  };

  const renderRiseStyle = (rise?: number): React.ReactNode => {
    if (rise === undefined) return '-';
    
    let color = '';
    if (rise > 0) {
      color = '#ef4444'; // red for positive
    } else if (rise < 0) {
      color = '#10b981'; // green for negative
    }
    
    const sign = rise > 0 ? '+' : '';
    return React.createElement('span', { style: { color } }, `${sign}${formatNumber(rise)}%`);
  };

  // Since we can't use antd components, we'll create a simplified version
  const renderTable = () => {
    return React.createElement(
      'div',
      { style: { overflowX: 'auto', padding: '20px' } },
      React.createElement(
        'table',
        {
          style: {
            width: '100%',
            borderCollapse: 'collapse',
            whiteSpace: 'nowrap'
          }
        },
        React.createElement(
          'thead',
          null,
          React.createElement(
            'tr',
            null,
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '交易所'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '股票编号'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '中文名'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '一年前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '一年内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '半年前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '半年内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '3个月前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '3个月内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '1月前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '1月内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '1周前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '1周内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '3日前收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '3日内涨幅'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '昨日收盘价'),
            React.createElement('th', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb', backgroundColor: '#f9fafb' } }, '昨日涨幅')
          )
        ),
        React.createElement(
          'tbody',
          null,
          data.map((item: StockMonitorData) => 
            React.createElement(
              'tr',
              { key: item.id },
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.exchange),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.stockCode),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.stockName),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price1yAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise1y)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price6mAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise6m)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price3mAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise3m)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price1mAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise1m)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price1wAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise1w)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.price3dAgo)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.rise3d)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, item.currencySymbol + formatNumber(item.priceYesterday)),
              React.createElement('td', { style: { padding: '12px 8px', textAlign: 'center', border: '1px solid #e5e7eb' } }, renderRiseStyle(item.riseYesterday))
            )
          )
        )
      )
    );
  };

  return React.createElement(
    'div',
    null,
    React.createElement(
      'div',
      {
        style: {
          maxWidth: '1400px',
          margin: '0 auto',
          background: '#fff',
          borderRadius: '8px',
          boxShadow: '0 2px 12px rgba(0,0,0,0.1)',
          overflow: 'hidden'
        }
      },
      React.createElement(
        'div',
        {
          style: {
            backgroundColor: '#1f2937',
            color: '#fff',
            padding: '16px 20px',
            fontSize: '18px',
            fontWeight: 600 as any
          }
        },
        "股票/指数涨幅监控列表"
      ),
      React.createElement(
        'div',
        {
          style: {
            textAlign: 'right' as any,
            padding: '10px 20px',
            fontSize: '12px',
            color: '#6b7280'
          }
        },
        cacheInfo
      ),
      loading ? 
        React.createElement(
          'div',
          { style: { textAlign: 'center', padding: '50px' } },
          "加载中..."
        ) : 
        renderTable()
    )
  );
};

export default MonitorList;