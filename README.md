# lasso
This project treat to link reference files to child files pushing new changes done in reference file to child file.

To invoke lasso library just put:

new Lasso(parentFile, childFile).execute();
If you don't want to overwrite the child file:

new Lasso(parentFile, childFile, false).execute();
