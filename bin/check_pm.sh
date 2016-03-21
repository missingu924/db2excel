#/bin/sh

. /export/home/nwom/.profile

cd $HOME/db2excel/bin

./db2excel.sh gsm_kpi_check.xml 
./db2excel.sh td_15m_kpi_check.xml 
./db2excel.sh td_h_kpi_check.xml 
