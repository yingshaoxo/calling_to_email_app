from auto_everything.terminal import Terminal
from auto_everything.disk import Disk
terminal = Terminal()
disk = Disk()


def run_it():
    import http.server

    audio_path = disk.join_paths(disk.get_directory_path(__file__), "yingshaoxo_gmail.mp3")


    def handle_url(sub_url: str) -> str:
        if (sub_url.endswith("play_post")):
            if ("mp3" not in terminal.run_command("ps x")):
                terminal.run(f"ffplay {audio_path} -autoexit -nodisp", wait=False)
            return "ok"

        if (sub_url.endswith("stop_post")):
            try:
                terminal.kill("ffplay")
            except Exception as e:
                print(e)
            return "ok"

        return "version 1.0"
    

    class WebRequestHandler(http.server.BaseHTTPRequestHandler):
        def do_GET(self):
            sub_url = self.path

            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()

            response = handle_url(sub_url)

            self.wfile.write(response.encode("utf-8"))

        def do_POST(self):
            self.do_GET()

    
    # Creating Server
    ServerClass  = http.server.HTTPServer
    
    # Defining protocol
    Protocol     = "HTTP/1.0"
    
    # Setting TCP Address
    port = 1919
    server_address = ('0.0.0.0', port)
    
    # invoking server
    http = ServerClass(server_address, WebRequestHandler)
    
    # Getting logs
    socket = http.socket.getsockname()
    print("Serving HTTP on", socket[0], "port", socket[1], "...")
    http.serve_forever()