#!/usr/bin/python3

# Assignment 2 COMP2041
# written by Nam Tran z5090191
# Assignment spec: cgi.cse.unsw.edu.au/~cs2041/assignments/UNSWtalk/

import os,re,shutil,glob,codecs,random,datetime
from flask import Flask, flash,render_template, session,request,url_for,redirect,send_from_directory
from werkzeug.utils import secure_filename
from email.mime.text import MIMEText
import subprocess

students_dir = "dataset-large";
ALLOWED_EXTENSIONS = set(['pdf', 'png', 'jpg', 'jpeg', 'gif'])
upload_folder = ""

app = Flask(__name__)

@app.route('/', methods=['GET'])
def start():
    return render_template('login.html')
#
@app.route('/login',methods=['GET','POST'])    
def login():
    session.clear()
    students = sorted(os.listdir(students_dir))
    username = request.form.get('username', '')
    inputPassword = request.form.get('inputPassword', '')
    username = re.sub(r'\D', '', username)
    
    studentDict = {}
    for student in students:
        studentDict[student] = os.path.join(students_dir,student,"student.txt")

    if username == '' and inputPassword == '': 
        return render_template('login.html')
    if username == '' or inputPassword == '':
        return render_template('login.html',messages="Username/password is missing")
    
    password=""
    if username != '':
        username = "z"+username
        if username not in studentDict.keys():
            return render_template('login.html',messages="zID does not exist")
        else:
            session['username'] = username
            with open(studentDict[username]) as f:
                lines = f.readlines()
            for line in lines:
                if line.startswith('password:'):
                    password = re.sub(r'password:\s*','',line)
            if inputPassword == '':
                return render_template('login.html',messages="Input your password")
            elif inputPassword != '': #if inputpass is not empty 
                password = re.sub(r'\s*$','',password)
                inputPassword = re.sub(r'\s*$','',inputPassword)
                if password  == inputPassword: #if same => let him log ins
                    students = sorted(os.listdir(students_dir))
                    student_to_show = username
                    details_filename = os.path.join(students_dir, student_to_show, "student.txt")
                    hashPost={}
                    hashComment={}

                    # IMAGE PROCESSING
                    studentPath = os.path.join(students_dir,student_to_show)
                    imageLink = os.path.join(students_dir,student_to_show,"img.jpg")
                    newImageName = username +".jpg"
                    newImageLink = os.path.join("static",newImageName)
                    if os.path.exists(imageLink):
                        shutil.copy(imageLink,newImageLink)
                    #FINISH IMAGE PROCESSING
                    message="";fromStudent="";dateTime="";full_name="";counter=0;date="";time="";timezone="";senderImageLink="";friendFullName="";duration=program=""
                    full_name=getFullName(username)
                    program=getProgram(username)
                    for file in glob.glob(studentPath+"/*.txt"):
                        if file != studentPath+"/student.txt":
                            #filter post
                            postFilter = re.sub(r'dataset-large/z\d{7}/','',file)
                            postFilter = re.sub(r'\.txt','',postFilter)
                            postFilter = postFilter.split("-")
                            if(len(postFilter) == 1):
                                
                                with open(file) as f:
                                    postFile = f.readlines()
                                for line in postFile:
                                    regex = re.compile(r'from:\s*')
                                    match = regex.search(line)
                                    if match is not None:
                                        fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                                        fromStudent = re.sub(r'\n','',fromStudent)
                                        senderImageLink=getImageLink(fromStudent)
                                     
                                    if line.startswith('time:'):
                                        dateTime = re.sub(r'time:\s*','',line)
                                        date = re.sub(r'T.*','',dateTime)
                                        date = re.sub(r'\n','',date)
                                        time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                                        timezone=re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                                        time = re.sub(r'\+.*','',time)
                                        
                                    if line.startswith('longitude:'):
                                        longitude = re.sub(r'longitude:\s*','',line)

                                    if line.startswith('latitude:'):
                                        latitude = re.sub(r'latitude:\s*','',line)

                                    if line.startswith('message:'):
                                        message = re.sub(r'message:\s*','',line)
                                  
                                    session['inputPassword'] = inputPassword
                                # print("This is date to debug",date)
                                #These are for the posts
                                if date != '':
                                        duration = getDuration(date)
                                if fromStudent != '':
                                        friendFullName = getFullName(fromStudent)
                                nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                                calendarInfo = [date,time,timezone]
                                hashComment = extractComment(studentPath,postFilter[0])
                                hashPost[file] = {'List' : nameAndImage,
                                      'Calendar': calendarInfo,
                                      'Message' : message,
                                      'Comment' : hashComment}
                    return render_template('homepage.html',hp=hashPost,newImageLink=newImageLink,full_name=full_name,program=program)
                elif password != inputPassword:
                    return render_template('login.html',messages="Incorrect password!")
                    
####################HOME PAGE###########################################
#######################################################################
@app.route('/homepage', methods=['GET','POST'])
def homepage():
    if 'username' not in session:
      return render_template('login.html')
    else:
      zid = session['username']
    students = sorted(os.listdir(students_dir))
    student_to_show = zid
    details_filename = os.path.join(students_dir, student_to_show, "student.txt")
    hashPost={}
    
    #IMAGE PROCESSING
    studentPath = os.path.join(students_dir,student_to_show)
    imageLink = os.path.join(students_dir,student_to_show,"img.jpg")
    newImageName = zid +".jpg"
    newImageLink = os.path.join("static",newImageName)
    if os.path.exists(imageLink):
        shutil.copy(imageLink,newImageLink)
    #FINISH IMAGE PROCESSING
    message="";fromStudent="";dateTime="";full_name="";
    date="";time="";timezone="";senderImageLink="";friendFullName="";duration="";program=""
    hashComment={}
    full_name=getFullName(zid)
    program=getProgram(zid)
    for file in glob.glob(studentPath+"/*.txt"):
        if file != studentPath+"/student.txt":
            #filter post
            postFilter = re.sub(r'dataset-large/z\d{7}/','',file)
            postFilter = re.sub(r'\.txt','',postFilter)
            postFilter = postFilter.split("-")
            if(len(postFilter) == 1):
                with open(file) as f:
                    postFile = f.readlines()
                for line in postFile:
                    if line.startswith('from:'):
                        fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                        fromStudent = re.sub(r'\n','',fromStudent)
                        senderImageLink=getImageLink(fromStudent)
                        # print(senderImageLink)
                    if line.startswith('time:'):
                        dateTime = re.sub(r'time:\s*','',line)
                        date = re.sub(r'T.*','',dateTime)
                        date = re.sub(r'\n','',date)
                        time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                        timezone = re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                        time = re.sub(r'\+.*','',time)

                    if line.startswith('longitude:'):
                        longitude = re.sub(r'longitude:\s*','',line)
                    
                    if line.startswith('latitude:'):
                        latitude = re.sub(r'latitude:\s*','',line)

                    if line.startswith('message:'):
                        message = re.sub(r'message:\s*','',line)
                    if date != '':
                     duration = getDuration(date)
                    if fromStudent != '':
                        friendFullName = getFullName(fromStudent)
                    nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                    calendarInfo = [date,time,timezone]
                    hashComment = extractComment(studentPath,postFilter[0])
                    
                    hashPost[file] = {'List' : nameAndImage,
                                      'Calendar': calendarInfo,
                                      'Message' : message,
                                      'Comment' : hashComment}
    return render_template('homepage.html',hp=hashPost,d=date,newImageLink=newImageLink,full_name=full_name,program=program)

#Display friend page when click on their name below the picture
#######################################################################
####################FRIEND'S PAGE#############################################
@app.route('/friendPage',methods=['GET','POST'])
def friendPage():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']

    friendID = request.form.get('submit')
    friendID=re.sub(r'([a-zA-Z]+\s*[a-zA-Z]+\s*\-\s*)','',friendID)
    friendID=re.sub(r'([a-zA-Z]+\s+)+','',friendID)
    students = sorted(os.listdir(students_dir))
    student_to_show = friendID
    details_filename = os.path.join(students_dir, student_to_show, "student.txt")
    hashPost={}
    studentPath = os.path.join(students_dir,student_to_show)
    message="";fromStudent="";dateTime="";full_name="";date="";time="";timezone="";senderImageLink="";friendFullName="";duration="";avatar="";program=""
    hashComment={}
    full_name=getFullName(friendID)
    program=getProgram(friendID)
    for file in glob.glob(studentPath+"/*.txt"):
        if file != studentPath+"/student.txt":
            #filter post
            postFilter = re.sub(r'dataset-large/z\d{7}/','',file)
            postFilter = re.sub(r'\.txt','',postFilter)
            postFilter = postFilter.split("-")
            if(len(postFilter) == 1):
                with open(file) as f:
                    postFile = f.readlines()
                for line in postFile:
                    if line.startswith('from:'):
                        fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                        fromStudent = re.sub(r'\n','',fromStudent)
                        senderImageLink=getImageLink(fromStudent)
                    if line.startswith('time:'):
                        dateTime = re.sub(r'time:\s*','',line)
                        date = re.sub(r'T.*','',dateTime)
                        date = re.sub(r'\n','',date)
                 
                        time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                        timezone=re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                        time = re.sub(r'\+.*','',time)
                    
                    if line.startswith('longitude:'):
                        longitude = re.sub(r'longitude:\s*','',line) 
                    if line.startswith('latitude:'):
                        latitude = re.sub(r'latitude:\s*','',line)
                    if line.startswith('message:'):
                        message = re.sub(r'message:\s*','',line)
                   
                    avatar = getImageLink(friendID) 
                    if date != "":
                      duration = getDuration(date)
                    if fromStudent != "":
                      friendFullName = getFullName(fromStudent)
                    nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                    calendarInfo = [date,time,timezone]
                    hashComment = extractComment(studentPath,postFilter[0])
                    hashPost[file] = {'List' : nameAndImage,
                                      'Calendar': calendarInfo,
                                      'Message' : message,
                                      'Comment' : hashComment}
    return render_template('friendPage.html',hp=hashPost,
                                            friendID=friendID,
                                            avatar=avatar,full_name=full_name,program=program)

##########################get information from student.txt##############
##########################PROFILE#######################################
@app.route('/profile', methods=['GET','POST'])
def profile():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    #n = session.get('n', 0)
    # print(zid)
    students = sorted(os.listdir(students_dir))
    student_to_show = zid
    details_filename = os.path.join(students_dir, student_to_show, "student.txt")
    studentPath = os.path.join(students_dir,student_to_show)
    ######### SET UP IMAGE
    hashImage = {}
    imageLink="";homesuburb="";friends="";homelatitude="";homelongitude=""
    for file in glob.glob(studentPath):
        # print(file)
        imageLink = os.path.join(students_dir,student_to_show,"img.jpg")
        #if os.path.exists(imageLink):
        newImageName = zid +".jpg"
        newImageLink = os.path.join("static",newImageName)    
        hashImage[zid] = newImageLink 
        # print(hashImage)
    if os.path.exists(imageLink):
        shutil.copy(imageLink,newImageLink)
    with open(details_filename) as f:
        details = f.readlines()
    for line in details:
        regex = re.compile(r'password:\s*')
        match = regex.search(line)
        if match is not None:
            password = re.sub(r'password:\s*','',line) #DONT PRINT        
        if line.startswith('zid'):
            zid = re.sub(r'zid:\s*','',line)
            zid = re.sub(r'\s*$','',zid)
        if line.startswith('email'):
            email = re.sub(r'email:\s*','',line)
        if line.startswith('full_name'):
            fullname = re.sub(r'full_name:\s*','',line)
        if line.startswith('birthday'):
            birthday = re.sub(r'birthday:\s*','',line)
        if line.startswith('home_suburb'):
            homesuburb = re.sub(r'home_suburb:\s*','',line)
        if line.startswith('home_longitude'):
            homelongitude = re.sub(r'home_longitude:\s*','',line)
        if line.startswith('home_latitude'):
            homelatitude = re.sub(r'home_latitude:\s*','',line)
        if line.startswith('friend:'):
            line = re.sub(r'friends:\s*','',line)
            line = re.sub(r'\(','',line)
            line = re.sub(r'\)','',line)
            friends = line.split(',\s*')
        if line.startswith('courses:'):
            line = re.sub(r'courses:\s*','',line)
            line = re.sub(r'\(','',line)
            line = re.sub(r'\)','',line)
            courses = line.split(',\s*')
    return render_template('profile.html',
                            em=email,
                            fn=fullname,bd=birthday,
                            fr=friends,cr=courses,
                            homesuburb=homesuburb,hlat=homelatitude,
                            hlo=homelongitude,ZID=zid,
                            ps=password,hashImage=hashImage)


###################display friend list######################################
####################FRIEND LIST#############################################
@app.route('/friends', methods=['GET','POST'])
def friends():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    students = sorted(os.listdir(students_dir))
    student_to_show = zid
    details_filename = os.path.join(students_dir, student_to_show, "student.txt")
    friends=[]
    with open(details_filename) as f:
        details = f.readlines()
    for line in details:
        regex = re.compile(r'friends:\s*')
        if regex.search(line) is not None:
            line = re.sub(r'friends:\s*','',line)
            line = re.sub(r'\(','',line)
            line = re.sub(r'\)','',line)
            friends = line.split(",")
        if line.startswith('full_name:'):
            friendFullName = re.sub(r'full_name:\s*','',line)
    friendsHash = {}
    hashFile = {}
    imageHash = {}
    for friend in friends:
        friend = re.sub(r',','',friend)
        friend = re.sub(r'\s*$','',friend)
        friend = re.sub(r'^\s*','',friend)
        friend_detail = os.path.join(students_dir, friend,"student.txt")
        friendDir = os.path.join(students_dir,friend)
        with open(friend_detail) as f:
            frienddetails = f.readlines()
        for line in frienddetails:
            if line.startswith('full_name:'):
                friendFullName = re.sub(r'full_name:\s*','',line)
        for files in glob.glob(friendDir):
            imageLinkF = os.path.join(students_dir,friend,"img.jpg")
            if os.path.exists(imageLinkF):
                newImageNameF = friend+".jpg"
                newImageLinkF = os.path.join("static",newImageNameF)    
                idAndName = friendFullName+" - "+friend
                imageHash[idAndName] = newImageLinkF 
                if os.path.exists(imageLinkF):
                    shutil.copy(imageLinkF,newImageLinkF)
                    idAndName = friendFullName+" - "+friend
                    imageHash[idAndName] = newImageLinkF
    return render_template('friends.html',
                            fr=friends,
                            hashImage=imageHash,
                            hashFile=hashFile)

###########################################################################
####################SEARCH BAR#############################################
@app.route('/search',methods=['POST'])
def search():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    student_to_show = zid
    
    searchInput = request.form.get('search')
    #search name in all the file
    searchID=""
    for friend in glob.glob(students_dir+"/*"):
        friend_detail = os.path.join(friend,"student.txt")
        with open(friend_detail) as f:
            lines = f.readlines()
        for line in lines:
            if line.startswith("full_name:"):
                friendName = re.sub(r'full_name:\s*','',line)
            if line.startswith('zid'):
                    searchID = re.sub(r'zid:\s*','',line)
                    searchID = re.sub(r'\s*$','',searchID)
        searchInputRegex = re.compile('%s'%searchInput)
         ###############IF MATCH THE NAME GO TO STUDENT PAGE
        if searchInputRegex.search(friendName) is not None:
            message="";fromStudent=avatar="";dateTime="";full_name="";date="";time="";timezone="";senderImageLink="";friendFullName="";duration=""
            hashComment={};hashPost={};
            for file in glob.glob(friend+"/*.txt"):
                postFilter = re.sub(r'dataset-large/z\d{7}/','',file)
                postFilter = re.sub(r'\.txt','',postFilter)
                postFilter = postFilter.split("-")
                if(len(postFilter) == 1):
                    # print(postFilter)
                    # print(file)
                    with open(file) as f:
                        postFile = f.readlines()
                 
                    for line in postFile:
                        if line.startswith('from:'):
                            fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                            fromStudent = re.sub(r'\n','',fromStudent)
                            senderImageLink=getImageLink(fromStudent)
                        if line.startswith('time:'):
                            dateTime = re.sub(r'time:\s*','',line)
                            date = re.sub(r'T.*','',dateTime)
                            date = re.sub(r'\n','',date)
                            time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                            timezone=re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                            time = re.sub(r'\+.*','',time)
                        if line.startswith('longitude:'):
                            longitude = re.sub(r'longitude:\s*','',line)
                        if line.startswith('latitude:'):
                            latitude = re.sub(r'latitude:\s*','',line)
                        if line.startswith('message:'):
                            message = re.sub(r'message:\s*','',line)
                        if line.startswith('full_name'):
                            fullname = re.sub(r'full_name:\s*','',line)
                        friendImage = searchID+".jpg"
                        avatar = os.path.join("static",friendImage)
                        if date != "":
                          duration = getDuration(date)
                        if fromStudent != "":
                          friendFullName = getFullName(fromStudent)
                        nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                        calendarInfo = [date,time,timezone]
                        hashComment = extractComment(friend,postFilter[0])
                        hashPost[file] = {'List' : nameAndImage,
                                          'Calendar': calendarInfo,
                                          'Message' : message,
                                          'Comment' : hashComment}
            return render_template('friendPage.html',hp=hashPost,
                                                friendID=searchID,
                                                avatar=avatar)
                                                
####################SIGN UP#############################################
#######################################################################
@app.route('/signUp',methods=['GET','POST'])
def signUp():
    session.clear()
    _zId = request.form.get('zID','')
    _fullName = request.form.get('full_name','')
    _studentEmail = request.form.get('email','')
    print(_studentEmail)
    _password = request.form.get('psw','')
    print(_password)
    _homeSuburb = request.form.get('home_suburb','')
    _courses = request.form.get('courses','')
    _birthday = request.form.get('birthday','')
    newFolder = _zId
    newPath = os.path.join(students_dir, newFolder)
    newPath = os.path.join(students_dir,newFolder)
    if not os.path.exists(newPath):
        os.makedirs(newPath)
        pathToStudentFile = os.path.join(newPath,"student.txt")
        # print(pathToStudentFile)
        studentNewFile = open(pathToStudentFile,"w+")
        studentNewFile.write("zid: %s\n" %(_zId))
        studentNewFile.write("full_name: %s\n" %(_fullName))
        studentNewFile.write("birthday: %s\n" %(_birthday))
        studentNewFile.write("email: %s\n" %_studentEmail)
        studentNewFile.write("password: %s\n" %_password)
        studentNewFile.write("home_suburb: %s\n" %_homeSuburb)
        studentNewFile.write("courses: %s\n" %_courses)
        
        currentDateTime = datetime.datetime.now()
        currentDateTime = re.sub(r'\..*','',str(currentDateTime))
        currentDateTime = re.sub(r' ','T',currentDateTime)
        currentDateTime += "+0000"
        studentPath = os.path.join(students_dir, _zId)
        newPostFileName =str(random.randrange(100, 1000000)) +".txt"
        pathToNewPost = os.path.join(studentPath,newPostFileName)
        creatingPost = open(pathToNewPost,"w+")
        creatingPost.write("from: %s\n" %(_zId))
        creatingPost.write("message: Hello UNSWtalk\n")
        creatingPost.write("time: %s\n" %(currentDateTime))
   
        session['email'] = _studentEmail
        session['full_name'] = _fullName
        message = "Thank You! You are now a member of UNSWtalk"
        subject = " UNSWtalk Confirmation email"
        send_email(_studentEmail,subject ,message)
    
        return render_template("signup_success.html",email=_studentEmail,fn=_fullName)
    else:
        return render_template("signup.html",message="Your account already exists!")

#Copy from  Evank (tutors) evank@cse.unsw.edu.au 
def send_email(to, subject, message):
    mutt = [
            'mutt',
            '-s',
            subject,
            '-e', 'set copy=no',
            '-e', 'set realname=UNSWtalk',
            '--', to
    ]
    subprocess.run(
            mutt,
            input = message.encode('utf8'),
            stderr = subprocess.PIPE,
            stdout = subprocess.PIPE,
    )
@app.route('/posting',methods=['POST'])
def posting():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    import datetime
    postContent = request.form.get('textInput')
    currentDateTime = datetime.datetime.now()
    currentDateTime = re.sub(r'\..*','',str(currentDateTime))
    currentDateTime = re.sub(r' ','T',currentDateTime)
    currentDateTime += "+0000"
    #go to student folder then create a new .txt file as post
    student_to_show = zid
    studentPath = os.path.join(students_dir, student_to_show)
    newPostFileName =str(random.randrange(100, 1000000)) +".txt"
    pathToNewPost = os.path.join(studentPath,newPostFileName)

    creatingPost = open(pathToNewPost,"w+")
    creatingPost.write("from: %s\n" %(zid))
    creatingPost.write("message: %s\n" %(postContent))
    creatingPost.write("time: %s\n" %(currentDateTime))
    return render_template("post.html")

#Function to delete post and any thing relate to that post
@app.route('/deletePost',methods=['POST'])
def deletePost():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    for key in request.form:
        #key here is file in form dataset-large/z5193755/5.txt[homepage|friendPage]
        print(key)
        fromPage=re.sub(r'dataset-large/z\d{7}/\d+\.txt','',key)
        key = re.sub(r'homepage','',key)
        key = re.sub(r'friendPage','',key)
        os.remove(key)
        studentPath=re.sub(r'\d\.txt','',key)
        firstDigit = re.sub(r'dataset-large/z\d{7}/','',key)
        firstDigit = re.sub(r'\.txt','',firstDigit)
        for file in glob.glob(studentPath+"/*.txt"):
            #filter post
            postFilter = re.sub(r'dataset-large/z\d{7}/','',file)
            postFilter = re.sub(r'\.txt','',postFilter)
            postFilter = postFilter.split("-")
            if postFilter[0] == firstDigit:
                os.remove(file)
    if fromPage == "homepage":
        return redirect(url_for(fromPage))
    else: 
        return render_template("post.html")
       
# Code base on  from Flask Document that check file extension is allowed or not
#flask.pocoo.org/docs/0.12/patterns/fileuploads/
def allowed_file(filename):
   return '.' in filename and \
      filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

#function to upload image/ change avatar of current user
@app.route('/uploadImage',methods=['POST'])
def uploadImage():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']

    if request.method == 'POST':
        if 'picture' not in request.files:
            flash('No file part')
            return redirect(url_for('uploadImage'))
        picture = request.files['picture']
    
    if picture.filename == '':
        flash('No selected file')
        return redirect(request.url)
    if picture and allowed_file(picture.filename):
        newPictureName = "img.jpg"
        upload_folder = os.path.join(students_dir,zid,newPictureName)
        picture.save(os.path.join(students_dir,zid,newPictureName))
        return render_template("post.html")

#function to delete current user account
@app.route('/deleteAccount',methods=['POST'])
def deleteAccount():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    path = os.path.join(students_dir,zid);
    shutil.rmtree(path);
    return render_template("post.html");

#delete friend from friend from friend list
@app.route('/unfriend',methods=['POST'])
def unfriend():
    if 'username' not in session:
        return render_template('login.html')
    else:
        zid = session['username']
    friendzId = ""
    for key in request.form:
        key = re.sub(r'\-','',key)
        key = re.sub(r'[a-zA-Z]+\s+','',key)
        print(key)
        friendzId = key
    studentTxt = os.path.join(students_dir,zid,"student.txt")
    with open(studentTxt) as f:
        lines=f.readlines()
        f.close()
    fw = open(studentTxt,"w")
    for line in lines:
        if line.startswith("friends"):
            print(line)
            line = re.sub(', %s'%friendzId,'',line)
            print(line)
            fw.write(line)
        else:
            fw.write(line)
    return render_template("post.html");
    
#Function to get student image link in static folder by zID
def getImageLink(zID):
    studentPath = os.path.join(students_dir,zID)
    imageLink=""
    for file in glob.glob(studentPath):
        imageLink = os.path.join(students_dir,zID,"img.jpg")
        newImageName = zID +".jpg"
        newImageLink = os.path.join("static",newImageName)    
    if os.path.exists(imageLink):
        shutil.copy(imageLink,newImageLink)
    else:
        newImageLink=os.path.join("static","default.jpg")
    return newImageLink

#Function to get a student full name by zID
def getFullName(zID):
   studentPath = os.path.join(students_dir,zID,"student.txt")
   with open(studentPath) as f:
      lines = f.readlines()
      full_name =""
      for line in lines:
         if line.startswith("full_name"):
            full_name=re.sub(r'full_name:\s*','',line)
   return full_name

#Function to get a student student program by zID
def getProgram(zID):
    studentPath = os.path.join(students_dir,zID,"student.txt")
    with open(studentPath) as f:
        lines = f.readlines()
        program=""
        for line in lines:
            if line.startswith("program"):
                program=re.sub(r'program:\s*','',line)
    return program

#function return diff between 2 dates value
def getDuration(date):
    returnVal = ""
    dateArray = date.split("-")
    formatedDate = datetime.date(int(dateArray[0]),int(dateArray[1]),int(dateArray[2]))
    today = datetime.date.today()
    duration = today-formatedDate
    if int(duration.days) < 365:
        returnVal = str(duration.days) +" days ago"
    else:
        returnVal = str(round(float(duration.days)/365,2))+ " years ago"
    return returnVal

#function to get the comment for the post file
def extractComment(studentPath,postFileDigit):
    commentFile=commentFileFilter=senderImageLink=message=calendarInfo=timezone=friendFullName=""
    hashReply={}
    hashComment={}
    for file in glob.glob(studentPath+"/*.txt"):
        if file != (studentPath+"/student.txt"):
            commentFileFilter = re.sub(r'dataset-large/z\d{7}/','',file)
            commentFileFilter = re.sub(r'\.txt','',commentFileFilter)
            commentFileFilter = commentFileFilter.split("-")
            if commentFileFilter[0] == postFileDigit and len(commentFileFilter) == 2:
                hashReply = extractReply(studentPath,postFileDigit,commentFileFilter[1])
                with open(file) as f:
                    commentFile = f.readlines()
                for line in commentFile:
                    if line.startswith('from:'):
                        fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                        fromStudent = re.sub(r'\n','',fromStudent)
                        senderImageLink=getImageLink(fromStudent)
                    if line.startswith('time:'):
                        dateTime = re.sub(r'time:\s*','',line)
                        date = re.sub(r'T.*','',dateTime)
                        date = re.sub(r'\n','',date)
                        time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                        timezone=re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                        time = re.sub(r'\+.*','',time)

                    if line.startswith('message:'):
                        message = re.sub(r'message:\s*','',line)
                    #Full-name in student.txt
                    if line.startswith('full_name:'):
                        full_name=re.sub(r'full_name:\s*','',line)    
                if date != '':
                    duration = getDuration(date)
                if fromStudent != '':
                    friendFullName = getFullName(fromStudent)
                nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                calendarInfo = [date,time,timezone]
                hashReply = extractReply(studentPath,postFileDigit,commentFileFilter[1])
                hashComment[file] = {'List' : nameAndImage,
                                    'Calendar': calendarInfo,
                                    'Message' : message,
                                    'HashReply' : hashReply}
    return hashComment 
      
#This return list of replies for the comment
def extractReply(studentPath,postFileDigit,commentFileDigit):
    commentFile=senderImageLink=message=calendarInfo=timezone=friendFullName=hashReply=""
    hashReply={}
    listOfReply = []
    for file in glob.glob(studentPath+"/*.txt"):
        if file != (studentPath+"/student.txt"):
            replyFileFilter = re.sub(r'dataset-large/z\d{7}/','',file)
            replyFileFilter = re.sub(r'\.txt','',replyFileFilter)
            replyFileFilter = replyFileFilter.split("-")
            if len(replyFileFilter)==3 and replyFileFilter[0] == postFileDigit and replyFileFilter[1] == commentFileDigit:
                with open(file) as f:
                    replyFile = f.readlines()
                for line in replyFile:
                    if line.startswith('from:'):
                            fromStudent = re.sub(r'from:\s*','',line) #DONT PRINT
                            fromStudent = re.sub(r'\n','',fromStudent)
                            senderImageLink=getImageLink(fromStudent)
                    if line.startswith('time:'):
                        dateTime = re.sub(r'time:\s*','',line)
                        date = re.sub(r'T.*','',dateTime)
                        date = re.sub(r'\n','',date)
                        time = re.sub(r'\d{4}-\d{2}-\d{2}T','',dateTime)
                        timezone=re.sub(r'\d{2}\:\d{2}:\d{2}','',time)
                        time = re.sub(r'\+.*','',time)
                    if line.startswith('message:'):
                        message = re.sub(r'message:\s*','',line)
                    #Full-name in student.txt
                    if line.startswith('full_name:'):
                        full_name=re.sub(r'full_name:\s*','',line)    
                if date != '':
                    duration = getDuration(date)
                if fromStudent != '':
                    friendFullName = getFullName(fromStudent)
                nameAndImage = [senderImageLink,friendFullName,fromStudent,duration]
                calendarInfo = [date,time,timezone]
                hashReply[file] = {'List' : nameAndImage,
                                  'Calendar': calendarInfo,
                                  'Message' : message }
    return hashReply
    
if __name__ == '__main__':
    app.secret_key = os.urandom(12)
    app.run(debug=True)
