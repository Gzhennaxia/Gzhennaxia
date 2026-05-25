/** 流水批量导入 CSV 模板（UTF-8 BOM，便于 Excel 打开） */
export const TRANSACTION_IMPORT_TEMPLATE_CSV = '\uFEFF类型,金额,账户,分类,交易时间,商户,备注,目标账户,标签\n'
  + '支出,88.50,支付宝,餐饮,2026-05-21 12:00:00,某某餐厅,午餐,,\n'
  + '收入,5000.00,招行工资卡,工资,2026-05-01 09:00:00,公司,月薪,,\n'
  + '转账,1000.00,招行工资卡,,2026-05-10 15:00:00,,还信用卡,信用卡,还款\n';

/**
 * 下载导入模板 CSV。
 */
export function downloadTransactionImportTemplate(): void {
  const blob = new Blob([TRANSACTION_IMPORT_TEMPLATE_CSV], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = '流水导入模板.csv';
  link.click();
  URL.revokeObjectURL(url);
}
