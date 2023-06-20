function gen {
  echo $(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w $1 | head -n 1)
}

echo $(gen 8)-$(gen 4)-$(gen 4)-$(gen 4)-$(gen 12)