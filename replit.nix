{pkgs}: {
  deps = [
    pkgs.ninja
    pkgs.cmake
    pkgs.wget
    pkgs.unzip
    pkgs.jdk17
    pkgs.android-tools
  ];
}
