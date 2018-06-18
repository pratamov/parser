-- Write SQL to find IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00.

SELECT `access_log_temp`.`ip`, `access_log_temp`.`requests` FROM (
	SELECT COUNT(`ip`) as `requests`, `ip`
	FROM `access_log` 
	WHERE (`date` BETWEEN '2017-01-01 13:00:00' AND '2017-01-01 14:00:00')
	GROUP BY `ip`
) as `access_log_temp` WHERE `access_log_temp`.`requests` > 100;

-- Write MySQL query to find requests made by a given IP (192.168.234.82)
SELECT COUNT(*) as `requests` FROM `access_log` WHERE `ip` LIKE '192.168.234.82';