#!/usr/bin/env /home/yingshaoxo/anaconda3/bin/python
from auto_everything.python import Python
from auto_everything.terminal import Terminal
from auto_everything.disk import Disk

from python_remote_audio_player import run_it

terminal = Terminal()

py = Python()
t = Terminal(debug=True)
disk = Disk()


class Tools():
    def __init__(self) -> None:
        pass

    def run(self):
        run_it()


py.make_it_global_runnable(executable_name="remote_player")
py.fire2(Tools)
