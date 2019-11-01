println() {
    # timestamp=`date +%Y%m%d%H%M%S`
    timestamp=$(date "+%Y-%m-%d %H:%M:%S")
    echo $timestamp $@
}

# Following regex is based on https://tools.ietf.org/html/rfc3986#appendix-B with
# additional sub-expressions to split authority into userinfo, host and port
#
readonly JDBC_URI_REGEX='^jdbc:mysql:(//((([^:/?#]+)@)?([^:/?#]+)(:([0-9]+))?))?(/([^?#]*))(\?([^#]*))?(#(.*))?'
#                    ↑↑            ↑  ↑↑↑            ↑         ↑ ↑            ↑ ↑        ↑  ↑        ↑ ↑
#                    |2 scheme     |  ||6 userinfo   7 host    | 9 port       | 11 rpath |  13 query | 15 fragment
#                    1 scheme:     |  |5 userinfo@             8 :…           10 path    12 ?…       14 #…
#                                  |  4 authority
#                                  3 //…

parse_host () {
    [[ "$@" =~ $JDBC_URI_REGEX ]] && echo "${BASH_REMATCH[5]}"
}

parse_port () {
    [[ "$@" =~ $JDBC_URI_REGEX ]] && echo "${BASH_REMATCH[7]}"
}

parse_dbname () {
    [[ "$@" =~ $JDBC_URI_REGEX ]] && echo "${BASH_REMATCH[9]}"
}