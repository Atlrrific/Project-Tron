activate_this = 'venv/bin/activate_this.py'
execfile(activate_this, dict(__file__=activate_this))

import sys.path.insert(0,'/root/Project-Tron')

from flask_app import app as application
