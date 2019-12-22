# git学习总结

svn中央仓库管理，存增量 ；git布式仓库管理，移动指针

## git三种状态

已修改  add   已暂存  commit  已提交

## 重要命令

#### 基本命令

ssh-keygen -t rsa -C "xxx@163.com"  生产秘钥

git init

.gitignore文件

git remote -v

git remote rename origin o

git remote remove o

git remote add origin git@github.com:guomzh/xxx.git

git clone git@github.com:guomzh/xxx.git

git add xx

git commit  -m '信息'

git diff 显示工作区与暂存区差异

git push

git status  查看工作区状态

git log  记录提交日志  保存在本地和远程

git reflog  记录所有 命令   只存在本地，不同步

#### 修改配置信息：

git config --global user.name "xxx"

git  config --add user.name guoming.zhang

git config --add user.email xxx@qunar.com

git config -e 查看配置信息

#### 高频率命令

1    git remote   、  git init  、  git   clone 见上文

2    git status   、 git log   、 git reflog  见上文

3    git config  见上文

4    git diff 、 git add 、 git commit 、 git push

git diff  查看工作区和本地仓库区别

git diff  --cached  、 git diff --staged  filename 查看暂存区与上一个commit区别

git diff  cid1 cid2  查看不同commit区别

git diff  分支1  分支2  查看不同分支区别

git push --set-upstream origin branch2  建立一个远程分支与本地对应并push

git push -u origin branch2  与上行效果一样

5    git fetch  、 git  merge 、 git pull

 git pull =  git fetch + git merge

git fetch

git merge  origin/branch2

git merge branch2  合并到本地另一分支如master

git merge  --abort 将合并取消

6 分支

git branch 名  、  git branch  -d 名 、 git branch -v详细信息

git checkout -b 分支名

git checkout --  文件名   丢弃工作区未add的修改

git checkout  commitid  a.txt   回退到某个版本 

git checkout HEAD~  a.txt  回退到上一个提交，head表示当前commit ,   HEAD~10表示回退到前10提交

7  reset

git reset HEAD  将本地仓库复制到暂存区，不改工作区内容

git reset --mixed HEAD~   将上一个commit复制到暂存区

git reset --hard HEAD~ 将上一个commit复制到暂存区和工作区

git reset --soft HEAD~ 将指针移到上一个commit而已

8 git  revert

git  revert  版本号， 撤销某次提交，也是一次提交   （或者git reset --hard 版本号，撤销Log找不到了）

9 git cherry-pick  版本号   用于把某次提交的内容合并到当前分支，而其他提交不合并，用git merge的话所有commit都合并

当冲突时，处理掉后，用  git  cherry-pick  --continue继续

10  git  rebase 分支名    用于把一个分支的修改合并到当前分支，串行，如修改提交了d',e',提取不同部分变为d' ，e'，master分支指针在d'前

