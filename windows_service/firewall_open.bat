set program_name="C:\Program Files (x86)\Java\jdk1.8.0_121\bin\java.exe"
netsh advfirewall firewall add rule name="sinoshuju" dir=in program=%program_name% action=allow protocol=tcp localport=9090
echo "firewall completed"