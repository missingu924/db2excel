#/bin/sh

LANG=zh_CN.GBK; export LANG
NLS_LANG=american_america.ZHS16GBK;export NLS_LANG

classpath='.'

#1��classpath�����Ӿ���Ӧ�ó��������jar��
for file in `ls ../lib/*.jar`
do
  classpath=$classpath:${file}
done

#2��classpath�����Ӿ���Ӧ�ó�������������ļ�Ŀ¼
classpath=../conf:../classes:$classpath

CURRENTPATH=`pwd`/.. ;export CURRENTPATH

java -Dprogram=DB2EXCEL_$CURRENTPATH -Xmx3G -XX:-UseGCOverheadLimit -classpath $classpath com.inspur.db2excel.Main "$@"
