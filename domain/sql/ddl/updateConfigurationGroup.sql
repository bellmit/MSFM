update configurationGroup set groupname = concat( '&1',ltrim(groupname,'&2') ) where groupName like '&2%';
