import React from 'react';

const LoadingSpinner: React.FC = () => {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '200px',
      position: 'relative'
    }}>
      <div style={{
        position: 'relative',
        width: '60px',
        height: '60px'
      }}>
        {/* 外圈 */}
        <div style={{
          position: 'absolute',
          width: '60px',
          height: '60px',
          border: '3px solid transparent',
          borderTop: '3px solid #667eea',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }} />
        
        {/* 内圈 */}
        <div style={{
          position: 'absolute',
          top: '10px',
          left: '10px',
          width: '40px',
          height: '40px',
          border: '2px solid transparent',
          borderTop: '2px solid #764ba2',
          borderRadius: '50%',
          animation: 'spin 0.8s linear infinite reverse'
        }} />
        
        {/* 中心点 */}
        <div style={{
          position: 'absolute',
          top: '25px',
          left: '25px',
          width: '10px',
          height: '10px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          borderRadius: '50%',
          animation: 'pulse 1.5s ease-in-out infinite'
        }} />
      </div>
      
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        
        @keyframes pulse {
          0%, 100% { transform: scale(1); opacity: 1; }
          50% { transform: scale(1.2); opacity: 0.7; }
        }
      `}</style>
    </div>
  );
};

export default LoadingSpinner;