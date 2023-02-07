DROP TABLE IF EXISTS limit_magnitude_area;

CREATE TABLE limit_magnitude_area (
                                      id              SERIAL 		PRIMARY KEY,
                                      corner_star     VARCHAR(7)	NOT NULL,
                                      count_stars     INT 		NOT NULL,
                                      limit_magnitude REAL    	NOT NULL
);