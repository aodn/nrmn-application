import csv

# Using the data in worms_taxonomy.csv, this script generates two SQL scripts:
# 1) Clears (deletes all rows from) the aphia_id table and repopulates it with
#    taxonomy data from worms_taxonomy.csv.
# 2) Updates taxonomy fields (down to family level) in the observable_item_ref table.
# Rows for which no information was returned from WoRMS are skipped.

INPUT_CSV = "worms_taxonomy.csv"
OUTPUT_SQL1 = "Taxonomy_Update.sql"
OUTPUT_SQL2 = "Aphia_Insert.sql"

RANK_FIELDS = [
    "rank_kingdom",
    "rank_phylum",
    "rank_class",
    "rank_order",
    "rank_family",
    "rank_genus",
]

INPUT_FIELDS = [
    "observable_item_id",
    "observable_item_name",
    "aphia_id",
    "url",
    "scientificname",
    "authority",
    "status",
    "unacceptreason",
    "taxon_rank_id",
    "rank",
    "valid_aphia_id",
    "valid_name",
    "valid_authority",
    "parent_name_usage_id",
    *RANK_FIELDS,
    "citation",
    "lsid",
    "is_marine",
    "is_brackish",
    "is_freshwater",
    "is_terrestrial",
    "is_extinct",
    "match_type",
    "modified",
]

def sql_value(v):
    """Convert Python value to safe SQL literal"""
    if v is None or v == "":
        return "NULL"
    return "'" + str(v).replace("'", "''") + "'"

def row_has_error(row):
    """Return True if any column contains 'error' (case-insensitive)"""
    return any(
        "error" in str(value).lower()
        for value in row.values()
        if value is not None
    )

def main():
    with open(INPUT_CSV, newline="", encoding="utf-8") as infile:
        reader = csv.DictReader(infile)
        all_rows = list(reader)

    # ✅ Filter once
    rows = [row for row in all_rows if not row_has_error(row)]
    print(f"Skipped {len(all_rows) - len(rows)} rows containing 'error'")

    # =============================
    # SQL FILE 1 – UPDATE
    # =============================
    with open(OUTPUT_SQL1, "w", encoding="utf-8") as f_update:
        f_update.write("BEGIN;\n")

        for row in rows:
            f_update.write(
                f"""
UPDATE nrmn.observable_item_ref
SET
    aphia_id = {sql_value(row.get("aphia_id"))},
    phylum = {sql_value(row.get("rank_phylum"))},
    class = {sql_value(row.get("rank_class"))},
    "order" = {sql_value(row.get("rank_order"))},
    family = {sql_value(row.get("rank_family"))}
WHERE observable_item_id = {sql_value(row.get("observable_item_id"))};
"""
            )

        f_update.write("COMMIT;\n")

    # =============================
    # SQL FILE 2 – DELETE + INSERT
    # =============================
    FIELDS_TO_DROP = {"observable_item_id", "observable_item_name"}
    APHIA_INSERT_FIELDS = [
        f for f in INPUT_FIELDS if f not in FIELDS_TO_DROP
    ]

    columns_sql = ", ".join(APHIA_INSERT_FIELDS)

    with open(OUTPUT_SQL2, "w", encoding="utf-8") as f_insert:
        f_insert.write("BEGIN;\n")
        f_insert.write("DELETE FROM nrmn.aphia_id;\n")

        for row in rows:
            values_sql = ", ".join(
                sql_value(row.get(col)) for col in APHIA_INSERT_FIELDS
            )

            f_insert.write(
                f"""
INSERT INTO nrmn.aphia_id ({columns_sql})
VALUES ({values_sql});
"""
            )

        f_insert.write("COMMIT;\n")

    print("Both SQL scripts generated successfully.")

if __name__ == "__main__":
    main()