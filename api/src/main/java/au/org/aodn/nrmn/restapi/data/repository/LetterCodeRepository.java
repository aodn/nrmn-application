package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.repository.projections.LetterCodeMapping;

@Repository
public interface LetterCodeRepository extends JpaRepository<Survey, Integer>, JpaSpecificationExecutor<Survey> {

    /**
     * This query consider the superseded_by and observable_item_name and return lettercode and observable_item_id
     * the species_name run through a couple of name to lettercode conversion, where it try to convert it to
     * lettercode of multiple length (3, 4, 5, max) and pick up the first non-null lettercode for use.
     *
     * @param methodId
     * @param siteIds
     * @return
     */
    @Query(value = "WITH stage0 AS (SELECT COALESCE(obsitem.superseded_by, obsitem.observable_item_name) AS species_name, obsitem.observable_item_id, SUM(obs.measure_value) AS abundance "
            + "FROM {h-schema}location_ref loc "
            + "INNER JOIN {h-schema}site_ref site_raw ON site_raw.location_id = loc.location_id "
            + "INNER JOIN {h-schema}survey sur ON sur.site_id = site_raw.site_id "
            + "INNER JOIN {h-schema}survey_method surmet ON surmet.survey_id = sur.survey_id "
            + "INNER JOIN {h-schema}observation obs ON obs.survey_method_id = surmet.survey_method_id "
            + "INNER JOIN {h-schema}observable_item_ref obsitem ON obsitem.observable_item_id = obs.observable_item_id "
            + "INNER JOIN {h-schema}methods_species ms ON obsitem.observable_item_id = ms.observable_item_id "
            + "WHERE site_raw.site_id in (:siteIds) AND ms.method_id = :methodId "
            + "GROUP BY obsitem.observable_item_id, species_name),"
            + "stage1 AS (SELECT oir.observable_item_id, species_name, abundance, {h-schema}abbreviated_species_code(species_name, 3) AS code_len3 FROM stage0 JOIN {h-schema}observable_item_ref oir on oir.observable_item_id=stage0.observable_item_id),"
            + "stage2 AS (SELECT *, ROW_NUMBER() OVER (partition by code_len3 order by abundance desc) AS code_len3_rank FROM stage1),"
            + "stage3 AS (SELECT *, CASE WHEN code_len3_rank = 1 THEN NULL ELSE {h-schema}abbreviated_species_code(species_name, 4) END code_len4 FROM stage2),"
            + "stage4 AS (SELECT *, ROW_NUMBER() OVER (partition by code_len4 order by abundance desc) AS code_len4_rank FROM stage3),"
            + "stage5 AS (SELECT *, CASE WHEN (code_len4 is NULL or code_len4_rank = 1) THEN NULL ELSE {h-schema}abbreviated_species_code(species_name, 5) END code_len5 FROM stage4),"
            + "stage6 AS (SELECT *, CAST(ROW_NUMBER() over (partition by code_len5 order by abundance desc) AS INT) AS code_len5_rank FROM stage5),"
            + "stage7 AS (SELECT *, CASE WHEN (code_len5 is NULL or code_len5_rank = 1) THEN NULL ELSE {h-schema}abbreviated_species_code(species_name, 5 + code_len5_rank - 1) END AS code_len_max FROM stage6) "
            + "SELECT oir.observable_item_id as observableItemId, "
            + "LOWER(COALESCE(s7.code_len_max, s7.code_len5, s7.code_len4, s7.code_len3)) AS letterCode "
            + "FROM {h-schema}observable_item_ref oir JOIN stage7 s7 on s7.observable_item_id = oir.observable_item_id "
            + "JOIN {h-schema}obs_item_type_ref oitr ON oitr.obs_item_type_id = oir.obs_item_type_id "
            + "WHERE oitr.obs_item_type_id = ANY (ARRAY[1, 2]) ORDER BY letterCode", nativeQuery = true)
    List<LetterCodeMapping> getForMethodWithSiteIds(@Param("methodId") Integer methodId, @Param("siteIds") List<Integer> siteIds);
}
