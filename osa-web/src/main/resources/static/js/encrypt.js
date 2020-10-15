/**
 * sha256密码加密，salt与后台保持一致
 * @returns
 */
function encrypt(clearPwd){
	var salt = '{null}';
	var cipherPwd = sha256_digest(clearPwd+salt);
	return cipherPwd;
}