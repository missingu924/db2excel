#/bin/sh
#���и�ϵͳ�˻���Ҫ���õĻ�����������Ҫ���ݸ���Ŀʵ������޸�
. /export/home/nwom/.profile

cd $HOME/db2excel/bin

LANG=zh_CN.GBK; export LANG
NLS_LANG=american_america.ZHS16GBK;export NLS_LANG

classpath='.'

for file in `ls ../lib/*.jar`
do
  classpath=$classpath:${file}
done

classpath=$classpath:../conf

java -classpath $classpath com.inspur.db2excel.Main "$@"
