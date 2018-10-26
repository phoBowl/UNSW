#!/usr/bin/python
import smtplib, subprocess, sys
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from_address = "andrewt@unsw.edu.au"
for png_file in sys.argv[1:]:
	subprocess.check_output(['echo', 'display', png_file])
	sys.stdout.write("Address to e-mail this image to? ")
	sys.stdout.flush()
	to_address = sys.stdin.readline().strip()
	if to_address:
		sys.stdout.write("Message to accompany image? ")
		sys.stdout.flush()
		message = sys.stdin.readline().strip()
		msg = MIMEMultipart(message)
		msg['Subject'] = png_file
		msg['From'] = from_address
		msg['To'] = to_address
		with open(png_file) as f:
			attachment = MIMEText(f.read())
			attachment.add_header('Content-Disposition', 'attachment', filename=png_file)
			msg.attach(attachment)
			s = smtplib.SMTP('smtp.cse.unsw.edu.au')
			s.sendmail(from_address, [to_address], msg.as_string())
			s.quit()
	else:
		print("No email sent")