#/bin/sh
#运行该系统账户需要设置的环境变量，需要根据各项目实际情况修改
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
