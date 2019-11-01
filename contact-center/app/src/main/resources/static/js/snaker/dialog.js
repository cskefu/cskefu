function selectOrg(ctx, e1, e2)
{
	var element1 = document.getElementById(e1);
	var element2 = document.getElementById(e2);
 	var l  = window.showModalDialog(ctx + "/dialogs/selectDialog.jsp?type=orgTree"," ","dialogWidth:800px;dialogHeight:540px;center:yes;scrolling:yes");
 	if (l == null )
 	    return;
 	var result = splitUsersAndAccounts(l);
 	element1.value = result[0];
 	element1.title = result[0];
 	element2.value = result[1];
}

function selectOrgUser(ctx, e1, e2)
{
	var element1 = document.getElementById(e1);
	var element2 = document.getElementById(e2);
 	var l  = window.showModalDialog(ctx + "/dialogs/selectDialog.jsp?type=orgUserTree"," ","dialogWidth:800px;dialogHeight:540px;center:yes;scrolling:yes");
 	if (l == null )
 	    return;
 	var result = splitUsersAndAccounts(l);
 	element1.value = result[0];
 	element1.title = result[0];
 	element2.value = result[1];
}

function splitUsersAndAccounts( userNamesAndAccount )
{
	var userNames = "";
	var accounts = "";

	var array = userNamesAndAccount.split( ";" );
	for(i=0; i<array.length; i++)
	{
		var temp = splitUserNameAndAccount(array[i]);
		userNames += temp[0] + ",";
		accounts += temp[1] + ",";
	}
	userNames = userNames.substr(0, userNames.length - 1);
	accounts = accounts.substr(0, accounts.length - 1);
	var result = new Array(2);
	result[0] = userNames;
	result[1] = accounts;
	return result;
}

function splitUserNameAndAccount( userNameAndAccount )
{
	var temp = new Array(2);
	if(userNameAndAccount.indexOf( "(" ) != -1)
	{
		temp[0] = userNameAndAccount.substring( 0,
      	userNameAndAccount.indexOf( "(" ) );
    	temp[1] = userNameAndAccount.substring( userNameAndAccount.indexOf( "(" ) + 1,
        userNameAndAccount.indexOf( ")" ) );
    }
    else
    {
    	temp[0] = userNameAndAccount;
    	temp[1] = userNameAndAccount;
    }
    return temp;
}